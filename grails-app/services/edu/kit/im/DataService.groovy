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
import java.text.DecimalFormat

class DataService {

  def springSecurityService

  // Get formatted list of daily consumption data for given date
  def getDailyData(DateTime date, boolean averages) {

    // Set lower end to midnight 00:00:00
    def low = date.withTimeAtStartOfDay()

    // Set upper end to one second before midnight 23:59:59
    def high = low.plusDays(1).minusSeconds(1)

    def dailyConsumptions = AggregatedConsumption.withCriteria() {
      between("intervalStart", low, high)
      eq("type", ConsumptionType.MIN5)
      household {
        eq("id", householdId())
      }
      order("intervalStart", "asc")
      projections {
        property("intervalStart")
        property("avgPowerPhase1")
        property("avgPowerPhase2")
        property("avgPowerPhase3")
      }
    }

    // Format data as [timestamp, powerValue]
    def formattedDailyConsumptions = dailyConsumptions.collect {
      DateTime intervalStart = (DateTime) it[0]
      BigDecimal avgPowerPhase1 = (BigDecimal) it[1]
      BigDecimal avgPowerPhase2 = (BigDecimal) it[2]
      BigDecimal avgPowerPhase3 = (BigDecimal) it[3]

      [intervalStart.getMillis() + 3600000, (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
    }

    // Create data for json
    def dataMap = ["consumption": formattedDailyConsumptions]

    // Additionally load average values
    if (averages) {
      // Collect all existing data from the same day, does not include current week
      // Use between() in to limit data range to a year or so with appropriate parameters
      def averageConsumptions = AggregatedConsumption.withCriteria() {
        lt("intervalStart", low)
        eq("type", ConsumptionType.MIN5)
        //eq("dayOfWeek", low.dayOfWeek)
        household {
          eq("id", householdId())
        }
        order("intervalStartTime", "asc")
        projections {
          groupProperty("intervalStartTime")
          avg("avgPowerPhase1")
          avg("avgPowerPhase2")
          avg("avgPowerPhase3")
        }
      }

      def formattedAverageConsumptions = averageConsumptions.collect {
        // Merge intervalStart with intervalStartTime
        LocalTime intervalStart = (LocalTime) it[0]
        DateTime mergedDate = low.withTime(intervalStart.hourOfDay, intervalStart.minuteOfHour, intervalStart.secondOfMinute, intervalStart.millisOfSecond)

        // Format data
        BigDecimal avgPowerPhase1 = new BigDecimal((Double) it[1])
        avgPowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase2 = new BigDecimal((Double) it[2])
        avgPowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase3 = new BigDecimal((Double) it[3])
        avgPowerPhase3.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        [mergedDate.getMillis() + 3600000, (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
      }

      dataMap["average"] = formattedAverageConsumptions
    }

    dataMap
  }

  // Get formatted list of weekly consumption data for given date
  def getWeeklyData(DateTime date, boolean averages) {

    // Set lower end to Monday midnight of current week, i.e. Mon, 00:00:00
    def low = date.withTimeAtStartOfDay().dayOfWeek().withMinimumValue()
    // Set upper end to one second before Monday midnight in one week, i.e. Sun, 23:59:59
    def high = low.plusWeeks(1).minusSeconds(1)

    def weeklyConsumptions = AggregatedConsumption.withCriteria() {
      between("intervalStart", low, high)
      eq("type", ConsumptionType.MIN30)
      household {
        eq("id", householdId())
      }
      order("intervalStart", "asc")
      projections {
        property("intervalStart")
        property("avgPowerPhase1")
        property("avgPowerPhase2")
        property("avgPowerPhase3")
      }
    }

    // Format data as [timestamp, powerValue]
    def formattedWeeklyConsumptions = weeklyConsumptions.collect {
      DateTime intervalStart = (DateTime) it[0]
      BigDecimal avgPowerPhase1 = (BigDecimal) it[1]
      BigDecimal avgPowerPhase2 = (BigDecimal) it[2]
      BigDecimal avgPowerPhase3 = (BigDecimal) it[3]

      [intervalStart.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
    }

    // Create data for json
    def dataMap = ["consumption": formattedWeeklyConsumptions]

    // Additionally load average values
    if (averages) {
      // Collect all existing data
      // Use between() in to limit data range to a year or so with appropriate parameters
      def averageConsumptions = AggregatedConsumption.withCriteria() {
        lt("intervalStart", low)
        eq("type", ConsumptionType.MIN30)
        household {
          eq("id", householdId())
        }
        order("dayOfWeek", "asc")
        order("intervalStartTime", "asc")
        projections {
          groupProperty("dayOfWeek")
          groupProperty("intervalStartTime")
          avg("avgPowerPhase1")
          avg("avgPowerPhase2")
          avg("avgPowerPhase3")
        }
      }

      def formattedAverageConsumptions = averageConsumptions.collect {
        // Merge intervalStart with intervalStartTime
        int dayOfWeek = (int) it[0]
        LocalTime intervalStart = (LocalTime) it[1]
        DateTime mergedDate = low.plusDays(dayOfWeek - 1).withTime(intervalStart.hourOfDay, intervalStart.minuteOfHour, intervalStart.secondOfMinute, intervalStart.millisOfSecond)

        // Format data
        BigDecimal avgPowerPhase1 = new BigDecimal((Double) it[2])
        avgPowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase2 = new BigDecimal((Double) it[3])
        avgPowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase3 = new BigDecimal((Double) it[4])
        avgPowerPhase3.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        [mergedDate.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
      }

      dataMap["average"] = formattedAverageConsumptions
    }

    dataMap
  }

  // Get formatted list of weekly consumption data for given date
  def getMonthlyData(DateTime date, boolean averages) {

    // Set lower end to midnight at first of current month, i.e. Mon, Jan 1, 00:00:00
    def low = date.withTimeAtStartOfDay().dayOfMonth().withMinimumValue()
    // Set upper end to one second before the end of the month , i.e. Sun, Jan 31, 23:59:59
    def high = low.plusMonths(1).minusSeconds(1)

    def weeklyConsumptions = AggregatedConsumption.withCriteria() {
      between("intervalStart", low, high)
      eq("type", ConsumptionType.H3)
      household {
        eq("id", householdId())
      }
      order("intervalStart", "asc")
      projections {
        property("intervalStart")
        property("avgPowerPhase1")
        property("avgPowerPhase2")
        property("avgPowerPhase3")
      }
    }

    // Format data as [timestamp, powerValue]
    def formattedWeeklyConsumptions = weeklyConsumptions.collect {
      DateTime intervalStart = (DateTime) it[0]
      BigDecimal avgPowerPhase1 = (BigDecimal) it[1]
      BigDecimal avgPowerPhase2 = (BigDecimal) it[2]
      BigDecimal avgPowerPhase3 = (BigDecimal) it[3]

      [intervalStart.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
    }

    // Create data for json
    def dataMap = ["consumption": formattedWeeklyConsumptions]

    // Additionally load average values
    if (averages) {
      // Collect all existing data
      // Use between() in to limit data range to a year or so with appropriate parameters
      def averageConsumptions = AggregatedConsumption.withCriteria() {
        lt("intervalStart", low)
        eq("type", ConsumptionType.H3)
        household {
          eq("id", householdId())
        }
        order("dayOfMonth", "asc")
        order("intervalStartTime", "asc")
        projections {
          groupProperty("dayOfMonth")
          groupProperty("intervalStartTime")
          avg("avgPowerPhase1")
          avg("avgPowerPhase2")
          avg("avgPowerPhase3")
        }
      }

      // Cache number of days in month for later use
      def daysInMonth = low.dayOfMonth().maximumValue

      def formattedAverageConsumptions = averageConsumptions.collect {
        // Merge intervalStart with intervalStartTime
        int dayOfMonth = (int) it[0]

        // In 30 day months, ignore 31st day
        if (dayOfMonth > daysInMonth) {
          return
        }

        LocalTime intervalStart = (LocalTime) it[1]
        DateTime mergedDate = low.withDayOfMonth(dayOfMonth).withTime(intervalStart.hourOfDay, intervalStart.minuteOfHour, intervalStart.secondOfMinute, intervalStart.millisOfSecond)

        // Format data
        BigDecimal avgPowerPhase1 = new BigDecimal((Double) it[2])
        avgPowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase2 = new BigDecimal((Double) it[3])
        avgPowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase3 = new BigDecimal((Double) it[4])
        avgPowerPhase3.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        [mergedDate.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
      }

      dataMap["average"] = formattedAverageConsumptions
    }

    dataMap
  }

  def getLiveData(def horizonLength) {
    def consumptions = Consumption.withCriteria() {
      household {
        eq("id", householdId())
      }
      order("date", "desc")
      projections {
        property("date")
        property("powerPhase1")
        property("powerPhase2")
        property("powerPhase3")
      }
      maxResults(new Integer(horizonLength))
    }

    Collections.reverse(consumptions)

    int i = 0
//    BigDecimal maximumValue = new BigDecimal(0.1)

    def phase1Data = consumptions.collect {
      DateTime date = (DateTime) it[0]

      // Format data
      BigDecimal powerPhase1 = new BigDecimal((Double) it[1])
      powerPhase1.setScale(3, RoundingMode.HALF_UP)

      [i++, powerPhase1]
    }

    i = 0
    def phase2Data = consumptions.collect {
      DateTime date = (DateTime) it[0]

      // Format data
      BigDecimal powerPhase2 = new BigDecimal((Double) it[2])
      powerPhase2.setScale(3, RoundingMode.HALF_UP)

      [i++, powerPhase2]
    }

    i = 0
    def phase3Data = consumptions.collect {
      DateTime date = (DateTime) it[0]

      // Format data
      BigDecimal powerPhase3 = new BigDecimal((Double) it[3])
      powerPhase3.setScale(3, RoundingMode.HALF_UP)

      [i++, powerPhase3]
    }

//    log.error returnData

    // Create data for json
    def dataMap = [:]

    dataMap["powerPhase1"] = phase1Data
    dataMap["powerPhase2"] = phase2Data
    dataMap["powerPhase3"] = phase3Data
    // TODO: Select top 1000 return last of top 10%
    dataMap["currentLevel"] = [[0, 1], [1, 1]]//[[0, returnData.last()[1] / maximumValue], [1, returnData.last()[1] / maximumValue]]

    dataMap
  }

  // Helper functions
  def householdId() {
    Household.get(springSecurityService.principal?.id)?.id
  }

}
