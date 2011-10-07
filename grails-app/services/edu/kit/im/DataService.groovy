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
import org.joda.time.LocalTime
import java.math.RoundingMode

class DataService {

  def springSecurityService

  // Get formatted list of daily consumption data for given date
  def getDailyData(DateTime date, boolean averages) {

    // Set lower end to midnight 00:00:00
    def low = date.withTimeAtStartOfDay()
    // Set upper end to midnight next day 00:00:00
    // Note that this technically wrong because is should be set to one second before midnight 23:59:59
    // However, for flot not to display an empty spot (line/bar rounding issue), this additional value is included
    def high = low.plusDays(1).minusSeconds(1) // 23:59:59

    def dailyConsumptions = AggregatedConsumption.withCriteria() {
      between("intervalStart", low, high)
      eq("type", ConsumptionType.MIN5)
      eq("macAddress", macAddress())
      order("intervalStart", "asc")
      projections {
        property("intervalStart")
        property("avgPowerReal")
      }
    }

    // Format data as [timestamp, powerValue]
    def formattedDailyConsumptions = dailyConsumptions.collect {
      DateTime intervalStart = (DateTime) it[0]
      BigDecimal avgPowerReal = (BigDecimal) it[1]

      [intervalStart.getMillis(), avgPowerReal]
    }

    // Create data for json
    def dataMap = ["daily": formattedDailyConsumptions]

    // Additionally load average values
    if (averages) {
      // Collect all existing data from the same day, does not include current week
      // Use between() in to limit data range to a year or so with appropriate parameters
      def averageConsumptions = AggregatedConsumption.withCriteria() {
        lt("intervalStart", low)
        eq("dayOfWeek", low.dayOfWeek)
        eq("type", ConsumptionType.MIN5)
        eq("macAddress", macAddress())
        order("intervalStartTime", "asc")
        projections {
          groupProperty("intervalStartTime")
          avg("avgPowerReal")
        }
      }

      def formattedAverageConsumptions = averageConsumptions.collect {
        // Merge intervalStart with intervalStartTime
        LocalTime intervalStart = (LocalTime) it[0]
        DateTime mergedDate = low.withTime(intervalStart.hourOfDay, intervalStart.minuteOfHour, intervalStart.secondOfMinute, intervalStart.millisOfSecond)

        // Format date
        BigDecimal avgPowerReal = new BigDecimal((Double) it[1])
        avgPowerReal.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        [mergedDate.getMillis(), avgPowerReal]
      }

      dataMap["average"] = formattedAverageConsumptions
    }

    dataMap
  }

  def macAddress() {
    Household.get(springSecurityService.principal?.id)?.macAddress
  }

}
