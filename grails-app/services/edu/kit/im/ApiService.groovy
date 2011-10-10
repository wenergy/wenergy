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

class ApiService {

  def processConsumption(def jsonPayload) {

    // Load and verify JSON content
    String macAddress
    BigDecimal powerReal
    BigDecimal powerReactive
    BigInteger timestamp

    // The following exceptions can throw since variables are statically typed
    try {
      macAddress = jsonPayload.id
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid MAC address type", 400)
    }

    try {
      powerReal = jsonPayload.p
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid real power type", 400)
    }

    try {
      powerReactive = jsonPayload.q
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid reactive power type", 400)
    }

    try {
      timestamp = jsonPayload.t
    } catch (GroovyCastException e) {
      throw new ApiException("Invalid timestamp type", 400)
    }

    if (!macAddress) throw new ApiException("No MAC address provided", 400)
    if (!powerReal) throw new ApiException("No real power provided", 400)
    if (!powerReactive) throw new ApiException("No reactive power provided", 400)
    if (!timestamp) throw new ApiException("No timestamp provided", 400)

    // Verify MAC address
    if (!(macAddress ==~ /([0-9a-f]{2}[:]){5}([0-9a-f]{2})/)) {
      throw new ApiException("Invalid MAC address format", 400)
    }

    // Get household
    def household = Household.findByMacAddress(macAddress, [cache: true])
    if (!household) throw new ApiException("Invalid MAC address", 400)

    // Verify timestamp
    def date = new DateTime(timestamp as long)
    // Simply sanity check to avoid wrong timestamps: year needs to be the same as the current year
    if (date.year() != new DateTime().year()) {
      throw new ApiException("Invalid timestamp", 400)
    }

    // All checks passed - create Consumption instance
    def consumption = new Consumption(household: household, date: date, powerReal: powerReal, powerReactive: powerReactive)

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
      aggregatedConsumption.sumPowerReal += consumption.powerReal
      aggregatedConsumption.avgPowerReal = aggregatedConsumption.sumPowerReal / aggregatedConsumption.consumptions.size()
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
      newAggregatedConsumption.sumPowerReal = consumption.powerReal
      newAggregatedConsumption.avgPowerReal = consumption.powerReal // / 1

      // Relationships
      newAggregatedConsumption.addToConsumptions(consumption)
      newAggregatedConsumption.save(failOnError: true)
    }
  }
}
