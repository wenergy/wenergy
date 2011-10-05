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
    def household = Household.findByMacAddress(macAddress)
    if (!household) throw new ApiException("Invalid MAC address", 400)

    // Verify timestamp
    def date = new DateTime(timestamp as long)
    // Simply sanity check to avoid wrong timestamps: year needs to be the same as the current year
    if (date.year() != new DateTime().year()) {
      throw new ApiException("Invalid timestamp", 400)
    }

    // All checks passed - create Consumption instance
    def consumption = new Consumption(macAddress: macAddress, powerReal: powerReal, powerReactive: powerReactive, date: date)
    household.addToConsumptions(consumption)

    try {
      household.save(failOnError: true)
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

  // Determine several aggregated values used for graphs (main reason is caching)
  def determineAggregation(Consumption consumption) {
    determine5minAggregation(consumption)
    determine30minAggregation(consumption)
    determine3hAggregation(consumption)
  }

  def determine5minAggregation(Consumption consumption) {
    // Check if consumption fits into existing aggregation interval
    def aggregatedConsumption = AggregatedConsumption.withCriteria(uniqueResult: true) {
      le("intervalStart", consumption.date)
      gt("intervalEnd", consumption.date)
      eq("type", ConsumptionType.MIN5)
      eq("macAddress", consumption.macAddress)
      maxResults(1)
    }

    if (aggregatedConsumption) {
      aggregatedConsumption.addToConsumptions(consumption)
      aggregatedConsumption.sumPowerReal += consumption.powerReal
      aggregatedConsumption.avgPowerReal = aggregatedConsumption.sumPowerReal / aggregatedConsumption.consumptions.size()
      aggregatedConsumption.save()
    }
    else {
      // Floor minutes
      def intervalStart = consumption.date.toMutableDateTime()
      int minutes = (int)(Math.floor(intervalStart.minuteOfHour / 5) * 5)
      intervalStart.setMinuteOfHour(minutes)
      intervalStart.setSecondOfMinute(0)

      // Ceil minutes
      def intervalEnd = consumption.date.toMutableDateTime()
      minutes = (int)(Math.floor((intervalEnd.minuteOfHour + 5) / 5) * 5)
      if (minutes >= 60) {
        minutes = 0
        intervalEnd.addHours(1)
      }
      intervalEnd.setMinuteOfHour(minutes)
      intervalEnd.setSecondOfMinute(0)

      def newConsumption = new AggregatedConsumption(type: ConsumptionType.MIN5, macAddress: consumption.macAddress)
      newConsumption.intervalStart = intervalStart.toDateTime()
      newConsumption.intervalEnd = intervalEnd.toDateTime()
      newConsumption.sumPowerReal = consumption.powerReal
      newConsumption.avgPowerReal = consumption.powerReal // / 1
      newConsumption.addToConsumptions(consumption)
      newConsumption.save()
    }
  }

  def determine30minAggregation(Consumption consumption) {

  }

  def determine3hAggregation(Consumption consumption) {

  }
}
