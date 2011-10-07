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

class DataService {

  def springSecurityService

  // Get formatted list of daily consumption data for given date
  def getDailyData(DateTime date) {

    // Set lower end to midnight 00:00:00
    def low = date.withTimeAtStartOfDay()
    // Set upper end to midnight next day 00:00:00
    // Note that this technically wrong because is should be set to one second before midnight 23:59:59
    // However, for flot not to display an empty spot (line/bar rounding issue), this additional value is included
    def high = low.plusDays(1)//.minusSeconds(1) // 23:59:59

    def aggregatedConsumptions = AggregatedConsumption.withCriteria() {
      between("intervalStart", low, high)
      eq("type", ConsumptionType.MIN5)
      eq("macAddress", macAddress())
      order("intervalStart", "asc")
    }

    // Format data as [timestamp, powerValue]
    return aggregatedConsumptions.collect { [it.intervalStart.getMillis(), it.avgPowerReal] }
  }

  def getDailyAverageData(DateTime date) {

    // Set lower end to midnight 00:00:00
    def low = date.withTimeAtStartOfDay()
    // Set upper end to midnight next day 00:00:00
    // Note that this technically wrong because is should be set to one second before midnight 23:59:59
    // However, for flot not to display an empty spot (line/bar rounding issue), this additional value is included
    def high = low.plusDays(1)//.minusSeconds(1) // 23:59:59

    try {
    def aggregatedConsumptions = AggregatedConsumption.withCriteria() {
      between("intervalStart", low, high)
      eq("type", ConsumptionType.MIN5)
      eq("macAddress", macAddress())
      order("intervalStart", "asc")
      projections {
        property("historicConsumption")
      }
    }

      /*

      daily     WeekdayConsumption - same weekday(1-7), same startInterval time
      weekly
      monthy

       */

      log.error aggregatedConsumptions




    } catch (Exception e) {
      log.error e
    }


    // Format data as [timestamp, powerValue]
    //return aggregatedConsumptions.collect { [it.intervalStart.getMillis(), it.avgPowerReal] }
  }

  def macAddress() {
    Household.get(springSecurityService.principal?.id)?.macAddress
  }
}
