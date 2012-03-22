/*
 * Copyright 2011 Institute of Information Engineering and Management,
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

import org.joda.time.DateTime
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import grails.validation.ValidationException
import org.joda.time.DateTimeZone

class ApiService {

  static transactional = 'mongo'

  def processConsumption(def jsonPayload) {

    // Load and verify JSON content
    Long deviceId
    BigDecimal powerPhase1
    BigDecimal powerPhase2
    BigDecimal powerPhase3
    BigDecimal batteryLevel
    BigInteger timestamp

    // The following exceptions can throw since variables are statically typed
    try {
      deviceId = jsonPayload.id
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid device id type", 400)
    }

    try {
      powerPhase1 = jsonPayload.p1
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid phase 1 power type", 400)
    }

    try {
      powerPhase2 = jsonPayload.p2
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid phase 2 power type", 400)
    }

    try {
      powerPhase3 = jsonPayload.p3
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid phase 3 power type", 400)
    }

    try {
      batteryLevel = jsonPayload.b
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
    def now = new DateTime()
    
    // Flot Bug Fix
    // We need to account for the Europe/Berlin timezone offset while keeping everything at UTC
    // Therefore, we add the time zone difference (+1 or +2 depending on DST) to the UTC date to simulate Europe/Berlin
    // So "now" is really UTC+1 or UTC+2 instead of UTC
    // Important: This results in inconsistent data. Any future data analysis should account for the additional time zone offset
    def hostTimezone = DateTimeZone.forTimeZone(TimeZone.getDefault()) // get host time zone, should be Europe/Berlin
    def offsetMillis = hostTimezone.getOffsetFromLocal(now.getMillis()) // local is UTC by default as set in BootStrap
    now = now.plusMillis(offsetMillis)

    def consumption = new Consumption(household: household, date: now, powerPhase1: powerPhase1,
        powerPhase2: powerPhase2, powerPhase3: powerPhase3, batteryLevel: batteryLevel)

    try {
      consumption.save(failOnError: true)
    } catch (ValidationException e) {
      throw new ApiException("Could not save consumption", 500)
    }

    // Post processing
    try {
      determineAggregation(consumption)
    } catch (Exception e) {
      throw new ApiException("Post processing error", 500)
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
}
