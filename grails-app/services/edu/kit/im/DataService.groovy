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

import java.math.RoundingMode
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat

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

      [intervalStart.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
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
        avgPowerPhase1 = avgPowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase2 = new BigDecimal((Double) it[2])
        avgPowerPhase2 = avgPowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase3 = new BigDecimal((Double) it[3])
        avgPowerPhase3 = avgPowerPhase3.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        [mergedDate.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
      }

      dataMap["average"] = formattedAverageConsumptions
    }

    dataMap
  }

  // Get formatted list of daily consumption data for given date  15min!
  def getDaily15Data(DateTime date, boolean averages) {

    // Set lower end to midnight 00:00:00
    def low = date.withTimeAtStartOfDay()

    // Set upper end to one second before midnight 23:59:59
    def high = low.plusDays(1).minusSeconds(1)

    def dailyConsumptions = AggregatedConsumption.withCriteria() {
      between("intervalStart", low, high)
      eq("type", ConsumptionType.MIN15)
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

      [intervalStart.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
    }

    // Create data for json
    def dataMap = ["consumption": formattedDailyConsumptions]

    // Additionally load average values
    if (averages) {
      // Collect all existing data from the same day, does not include current week
      // Use between() in to limit data range to a year or so with appropriate parameters
      def averageConsumptions = AggregatedConsumption.withCriteria() {
        lt("intervalStart", low)
        eq("type", ConsumptionType.MIN15)
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
        avgPowerPhase1 = avgPowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase2 = new BigDecimal((Double) it[2])
        avgPowerPhase2 = avgPowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase3 = new BigDecimal((Double) it[3])
        avgPowerPhase3 = avgPowerPhase3.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        [mergedDate.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
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
        avgPowerPhase1 = avgPowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase2 = new BigDecimal((Double) it[3])
        avgPowerPhase2 = avgPowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase3 = new BigDecimal((Double) it[4])
        avgPowerPhase3 = avgPowerPhase3.setScale(3, RoundingMode.HALF_UP)

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
        avgPowerPhase1 = avgPowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase2 = new BigDecimal((Double) it[3])
        avgPowerPhase2 = avgPowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal avgPowerPhase3 = new BigDecimal((Double) it[4])
        avgPowerPhase3 = avgPowerPhase3.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        [mergedDate.getMillis(), (avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3)]
      }

      dataMap["average"] = formattedAverageConsumptions
    }

    dataMap
  }

  def getLastConsumption() {
    def consumptions = Consumption.withCriteria() {
      household {
        eq("id", householdId())
      }
      projections {
        property("powerPhase1")
        property("powerPhase2")
        property("powerPhase3")
      }
      maxResults(new Integer(1))
    }
    BigDecimal powerPhase1 = new BigDecimal((Double) consumptions[0])
    BigDecimal powerPhase2 = new BigDecimal((Double) consumptions[1])
    BigDecimal powerPhase3 = new BigDecimal((Double) consumptions[2])

    def BigDecimal returnValue = powerPhase1 + powerPhase2 + powerPhase3
  }

  def getLastConsumptionById(id) {
    def consumptions = Consumption.withCriteria() {
      household {
        eq("id", id)
      }
      projections {
        property("powerPhase1")
        property("powerPhase2")
        property("powerPhase3")
      }
      maxResults(new Integer(1))
    }
    BigDecimal powerPhase1 = new BigDecimal((Double) consumptions[0])
    BigDecimal powerPhase2 = new BigDecimal((Double) consumptions[1])
    BigDecimal powerPhase3 = new BigDecimal((Double) consumptions[2])

    def BigDecimal returnValue = powerPhase1 + powerPhase2 + powerPhase3
  }

  def getLiveData(int numberOfValues, DateTime deltaTime) {
    // Create data for json
    def dataMap = [:]

    // Always return current server time
    dataMap["serverTime"] = new DateTime().getMillis()

    // Load consumptions
    def consumptions = Consumption.withCriteria() {
      if (deltaTime) {
        gt("date", deltaTime)
      }
      household {
        eq("id", householdId())
      }
      order("date", "desc")
      projections {
        property("date")
        property("powerPhase1")
        property("powerPhase2")
        property("powerPhase3")
        property("batteryLevel")
      }
      maxResults(numberOfValues)
    }

    Collections.reverse(consumptions)

    // Prepare consumption data
    def phase1Data = []
    def phase2Data = []
    def phase3Data = []

    consumptions.each { consumption ->
      DateTime date = (DateTime) consumption[0]

      // Format data
      BigDecimal powerPhase1 = new BigDecimal((Double) consumption[1])
      powerPhase1 = powerPhase1.setScale(3, RoundingMode.HALF_UP)

      BigDecimal powerPhase2 = new BigDecimal((Double) consumption[2])
      powerPhase2 = powerPhase2.setScale(3, RoundingMode.HALF_UP)

      BigDecimal powerPhase3 = new BigDecimal((Double) consumption[3])
      powerPhase3 = powerPhase3.setScale(3, RoundingMode.HALF_UP)

      DateTimeFormatter formatter = DateTimeFormat.mediumDateTime().withLocale(Locale.GERMAN)
      def formattedDate = formatter.print(date)

      // Highcharts data point objects
      phase1Data << ["name": formattedDate, "y": powerPhase1]
      phase2Data << ["name": formattedDate, "y": powerPhase2]
      phase3Data << ["name": formattedDate, "y": powerPhase3]
    }

    dataMap["phase1Data"] = phase1Data
    dataMap["phase2Data"] = phase2Data
    dataMap["phase3Data"] = phase3Data

    if (deltaTime) {
      dataMap["isDelta"] = true
    }

    // Battery level from last consumption
    if (consumptions.size() > 0) {
      def consumption = consumptions.last()
      def maxBatteryLevel = 3200

      BigDecimal batteryLevel = new BigDecimal((Double) consumption[4])
      batteryLevel = batteryLevel / maxBatteryLevel * 100.0
      batteryLevel = batteryLevel.min(100.0)
      batteryLevel = batteryLevel.setScale(2, RoundingMode.HALF_UP)

      dataMap["batteryLevel"] = "$batteryLevel %"
    }

//    def currentConsumption = phase1Data.last()[1]+phase2Data.last()[1]+phase3Data.last()[1]
//    def referenceValue = Math.max(Household.findById(householdId())?.referenceConsumption ?: 0.0,0.01)
//    dataMap["currentLevel"] = [[0, currentConsumption / referenceValue], [1, currentConsumption / referenceValue]]

    dataMap
  }

  // Helper functions
  def householdId() {
    Household.get(springSecurityService.principal?.id)?.id
  }

}
