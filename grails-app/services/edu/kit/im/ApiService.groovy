/*
 * Copyright 2011-2012 Institute of Information Engineering and Management,
 * Information & Market Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.kit.im

import edu.kit.im.messages.ApiErrorMessage
import edu.kit.im.messages.ConsumptionMessage
import grails.converters.JSON
import grails.validation.ValidationException
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import edu.kit.im.enums.ConsumptionType
import edu.kit.im.utils.DateUtils
import edu.kit.im.exceptions.ApiException

class ApiService {

  static rabbitQueue = "api"

  def householdService

  void handleMessage(ConsumptionMessage message) {
    try {
      def jsonObject = JSON.parse(message.json)
      processConsumption(message, jsonObject)
    } catch (Exception e) {
      rabbitSend "api", new ApiErrorMessage(e.message, message.clientIp, null, message.json)
    }
  }

  void handleMessage(ApiErrorMessage message) {
    def apiError = new ApiError()
    apiError.description = message.description
    apiError.clientIp = message.clientIp
    apiError.householdId = message.householdId
    apiError.json = message.json
    try {
      apiError.save(failOnError: true)
    } catch (ValidationException e) {
      log.error apiError.errors
    }
  }

  def processConsumption(def message, def jsonObject) {
    // Process error first
    if (jsonObject.error) {
      def error = jsonObject.error

      if (error.id && error.code) {
        def description = "Nanode: ${error.code?.toUpperCase()}"
        rabbitSend "api", new ApiErrorMessage(description, message.clientIp, error.id as String, message.json)
      } else {
        throw new ApiException("Invalid error", 400)
      }

      return
    }

    // Load and verify JSON content
    Long deviceId
    BigDecimal powerPhase1
    BigDecimal powerPhase2
    BigDecimal powerPhase3
    BigDecimal batteryLevel
    BigInteger timestamp

    // The following exceptions can throw since variables are statically typed
    try {
      deviceId = jsonObject.id
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid device id type", 400)
    }

    try {
      powerPhase1 = jsonObject.p1
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid phase 1 power type", 400)
    }

    try {
      powerPhase2 = jsonObject.p2
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid phase 2 power type", 400)
    }

    try {
      powerPhase3 = jsonObject.p3
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid phase 3 power type", 400)
    }

    try {
      batteryLevel = jsonObject.b
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid battery level type", 400)
    }

    if (deviceId == null) throw new ApiException("No device id provided", 400)
    if (powerPhase1 == null) throw new ApiException("No phase 1 power provided", 400)
    if (powerPhase2 == null) throw new ApiException("No phase 2 power provided", 400)
    if (powerPhase3 == null) throw new ApiException("No phase 3 power provided", 400)
    if (batteryLevel == null) throw new ApiException("No battery level provided", 400)

    // Get household
    def household = Household.findByDeviceId(deviceId, [cache: true])
    if (!household) throw new ApiException("Invalid device id", 400)

    // All checks passed - create Consumption instance
    def now = DateUtils.addUTCOffset(message.dateTime)

    def consumption = new Consumption(household: household, date: now, powerPhase1: powerPhase1,
        powerPhase2: powerPhase2, powerPhase3: powerPhase3, batteryLevel: batteryLevel)

    try {
      consumption.save(failOnError: true)
    } catch (ValidationException e) {
      log.error consumption.errors
      throw new ApiException("Could not save consumption", 500)
    }

    // Post processing
    try {
      determineAggregation(consumption)
    } catch (Exception e) {
      log.error e
      throw new ApiException("Aggregation processing error", 500)
    }

    try {
      // Set reference value once
      if (household.referenceConsumptionValue == null) {
        householdService.determineReferenceConsumptionValue(household.id)
      }
    } catch (Exception e) {
      log.error e
      throw new ApiException("Reference value processing error", 500)
    }

    try {
      // Cache power level
      def referenceConsumptionValue = household.referenceConsumptionValue
      BigDecimal sumPower = powerPhase1 + powerPhase2 + powerPhase3
      def powerLevel = (referenceConsumptionValue > 0) ? sumPower / referenceConsumptionValue : 0.0
      powerLevel = powerLevel.max(0.0)

      household.currentPowerLevelValue = powerLevel
      household.save()
    } catch (Exception e) {
      log.error e
      throw new ApiException("Power level processing error", 500)
    }
  }

  // Automatically aggregate all defined interval types
  def determineAggregation(Consumption consumption) {
    for (type in ConsumptionType.values()) {
      determineAggregation(consumption, type)
    }
  }

  // Determine aggregated values used for graphs (main reason is caching)
  def determineAggregation(Consumption consumption, ConsumptionType type) {
    // Check if consumption fits into existing aggregation interval
    def aggregatedConsumption = AggregatedConsumption.withCriteria(uniqueResult: true) {
      le("intervalStart", consumption.date)
      gt("intervalEnd", consumption.date)
      eq("type", type)
      household {
        eq("id", consumption.household.id)
      }
      maxResults(1)
    }

    if (aggregatedConsumption) {
      // Already exists, so just save and update power values
      aggregatedConsumption.addToConsumptions(consumption)
      aggregatedConsumption.sumPowerPhase1 += consumption.powerPhase1
      aggregatedConsumption.avgPowerPhase1 = aggregatedConsumption.sumPowerPhase1 / aggregatedConsumption.consumptions.size()
      aggregatedConsumption.sumPowerPhase2 += consumption.powerPhase2
      aggregatedConsumption.avgPowerPhase2 = aggregatedConsumption.sumPowerPhase2 / aggregatedConsumption.consumptions.size()
      aggregatedConsumption.sumPowerPhase3 += consumption.powerPhase3
      aggregatedConsumption.avgPowerPhase3 = aggregatedConsumption.sumPowerPhase3 / aggregatedConsumption.consumptions.size()
      aggregatedConsumption.save()
    }
    else {
      // Determine interval
      int period = type.minutes()
      def intervalStart = DateUtils.floorDateMinutes(consumption.date, period)
      def intervalEnd = DateUtils.ceilDateMinutes(consumption.date, period)
      // Save new aggregated consumption
      def newAggregatedConsumption = new AggregatedConsumption(type: type, household: consumption.household)
      newAggregatedConsumption.intervalStart = intervalStart
      newAggregatedConsumption.intervalEnd = intervalEnd
      newAggregatedConsumption.intervalStartTime = intervalStart.toLocalTime()
      newAggregatedConsumption.intervalEndTime = intervalEnd.toLocalTime()
      newAggregatedConsumption.dayOfWeek = intervalStart.dayOfWeek
      newAggregatedConsumption.dayOfMonth = intervalStart.dayOfMonth
      newAggregatedConsumption.sumPowerPhase1 = consumption.powerPhase1
      newAggregatedConsumption.avgPowerPhase1 = consumption.powerPhase1 // / 1
      newAggregatedConsumption.sumPowerPhase2 = consumption.powerPhase2
      newAggregatedConsumption.avgPowerPhase2 = consumption.powerPhase2 // / 1
      newAggregatedConsumption.sumPowerPhase3 = consumption.powerPhase3
      newAggregatedConsumption.avgPowerPhase3 = consumption.powerPhase3 // / 1

      // Relationships
      newAggregatedConsumption.addToConsumptions(consumption)
      newAggregatedConsumption.save(failOnError: true)
    }
  }

  def getClientIP(def request) {
    def realIp = request?.getHeader("X-Real-IP")
    def forwardedIP = request?.getHeader("X-Forwarded-For")?.tokenize(",")?.first()?.trim()
    def remoteAddr = request?.getRemoteAddr()

    // Prioritize return value
    realIp ?: (forwardedIP ?: (remoteAddr ?: ""))
  }
}
