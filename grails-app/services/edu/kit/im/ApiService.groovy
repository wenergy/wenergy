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

import edu.kit.im.enums.ConsumptionType
import edu.kit.im.exceptions.ApiException
import edu.kit.im.messages.ApiErrorMessage
import edu.kit.im.utils.DateUtils
import grails.validation.ValidationException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

class ApiService {

  def householdService

  def validateConsumption(def jsonObject) {
    // Load and verify JSON content

    if (jsonObject.error) {
      def error = jsonObject.error
      if (!(jsonObject.error instanceof JSONObject) || !error.id || !error.code) {
        throw new ApiException("Invalid error", 400)
      }
      return
    }

    Long deviceId
    BigDecimal powerPhase1
    BigDecimal powerPhase2
    BigDecimal powerPhase3
    BigDecimal batteryLevel

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
  }

  def processConsumption(def message, def jsonObject) {
    // Process error first
    if (jsonObject.error) {
      def error = jsonObject.error

      if (error.id && error.code) {
        def description = "Nanode: ${error.code?.toUpperCase()}"
        rabbitSend "wenergy", "db", new ApiErrorMessage(description, message.clientIp, error.id as String, message.json)
      }

      return
    }

    // Load JSON content
    Long deviceId = jsonObject.id
    BigDecimal powerPhase1 = jsonObject.p1
    BigDecimal powerPhase2 = jsonObject.p2
    BigDecimal powerPhase3 = jsonObject.p3
    BigDecimal batteryLevel = jsonObject.b

    // Get household
    def household = Household.findByDeviceId(deviceId, [cache: true])
    if (!household) return error("Invalid device id", message)

    // All checks passed - create Consumption instance
    def now = DateUtils.addUTCOffset(message.dateTime)

    def consumption = new Consumption(household: household, date: now, powerPhase1: powerPhase1,
        powerPhase2: powerPhase2, powerPhase3: powerPhase3, batteryLevel: batteryLevel)

    try {
      consumption.save()
    } catch (ValidationException e) {
      log.error consumption.errors
      return error("Could not save consumption", message)
    }

    // Post processing
    try {
      determineAggregation(consumption)
    } catch (Exception e) {
      log.error e
      return error("Aggregation processing error", message)
    }

    try {
      // Set reference value once
      if (household.referenceConsumptionValue == null) {
        householdService.determineReferenceConsumptionValue(household.id)
      }
    } catch (Exception e) {
      log.error e
      return error("Reference value processing error", message)
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
      return error("Power level processing error", 500)
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
      newAggregatedConsumption.save()
    }
  }

  def getClientIP(def request) {
    def realIp = request?.getHeader("X-Real-IP")
    def forwardedIP = request?.getHeader("X-Forwarded-For")?.tokenize(",")?.first()?.trim()
    def remoteAddr = request?.getRemoteAddr()

    // Prioritize return value
    realIp ?: (forwardedIP ?: (remoteAddr ?: ""))
  }

  def error(def description, def message) {
    rabbitSend "wenergy", "db", new ApiErrorMessage(description, message.clientIp, null, message.json)
    null
  }
}
