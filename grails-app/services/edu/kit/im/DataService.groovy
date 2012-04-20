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

class DataService {

  def springSecurityService

  // Get power level indicator information
  def getWelcomeData() {
    def dataMap = [:]
    def powerLevels = []

    // Get all households
    Household.getAll().each {h ->
      // Get latest consumption
      def consumption = Consumption.withCriteria() {
        household {
          eq("id", h.id)
        }
        order("date", "desc")
        projections {
          property("powerPhase1")
          property("powerPhase2")
          property("powerPhase3")
        }
        maxResults(1)
      }

      if (consumption.size()) {
        def consumptionVal = consumption[0]
        BigDecimal powerPhase1 = (BigDecimal) consumptionVal[0]
        BigDecimal powerPhase2 = (BigDecimal) consumptionVal[1]
        BigDecimal powerPhase3 = (BigDecimal) consumptionVal[2]
        BigDecimal sumPower = powerPhase1 + powerPhase2 + powerPhase3

        def referenceValue = h.referenceConsumptionValue
        def powerLevel = (referenceValue > 0) ? sumPower / referenceValue : 0.0
        powerLevel = powerLevel.max(0.0)
        powerLevel = powerLevel.setScale(2, RoundingMode.HALF_UP)

        if (powerLevel > 0) {
          powerLevels << powerLevel
        }
      }
    }

    dataMap["powerLevels"] = powerLevels

    dataMap
  }

  // Consumption data handler
  def getConsumptionData(DateTime date, String interval, Integer precision, String dataType, DateTime deltaTime) {
    // Declare common variables
    def low
    def high
    ConsumptionType type
    def dataMap = [:]

    // Always return current server time
    dataMap["serverTime"] = new DateTime().getMillis()

    if (deltaTime) {
      dataMap["isDelta"] = true
    }

    // Set date boundaries
    switch (interval) {
      case "daily":
        low = date.withTimeAtStartOfDay() // Set lower end to midnight 00:00:00
        high = low.plusDays(1).minusSeconds(1) // Set upper end to one second before midnight 23:59:59
        type = (precision == 15) ? ConsumptionType.MIN15 : ConsumptionType.MIN5
        break
      case "weekly":
        low = date.withTimeAtStartOfDay().dayOfWeek().withMinimumValue() // Set lower end to Monday midnight of current week, i.e. Mon, 00:00:00
        high = low.plusWeeks(1).minusSeconds(1) // Set upper end to one second before Monday midnight in one week, i.e. Sun, 23:59:59
        type = ConsumptionType.MIN30
        break
      case "monthly":
        low = low = date.withTimeAtStartOfDay().dayOfMonth().withMinimumValue() // Set lower end to midnight at first of current month, i.e. Mon, Jan 1, 00:00:00
        high = low.plusMonths(1).minusSeconds(1) // Set upper end to one second before the end of the month , i.e. Sun, Jan 31, 23:59:59
        type = ConsumptionType.H3
        break
      default:
        throw new Exception("Invalid interval specified.")
    }

    // Also return low and high
    dataMap["time"] = ["low": low.getMillis(), "high": high.getMillis()]

    // Get consumptions
    def consumptions = AggregatedConsumption.withCriteria() {
      if (deltaTime) {
        gt("dateCreated", deltaTime)
      }
      between("intervalStart", low, high)
      eq("type", type)
      household {
        eq("id", householdId())
      }
      order("intervalStart", "asc")
      projections {
        property("intervalStart")
        property("intervalEnd")
        property("avgPowerPhase1")
        property("avgPowerPhase2")
        property("avgPowerPhase3")
      }
    }

    // Format consumptions according to dataType
    switch (dataType) {
      case "averages":
        dataMap["consumptionData"] = formatConsumptions(consumptions)
        // Power level
        dataMap.putAll(getPowerLevel(dataMap["consumptionData"]))
        break
      case "phases":
        dataMap.putAll(splitConsumptionsIntoPhases(consumptions, true, true))
        // Power level
        dataMap.putAll(getPowerLevel(dataMap))
        break
      default:
        throw new Exception("Invalid dataType specified.")
    }

    // Average values
    if (dataType == "averages") {
      // Collect all existing data
      // Use between() in to limit data range to a year or so with appropriate parameters
      def averageConsumptions = AggregatedConsumption.withCriteria() {
        if (deltaTime) {
          gt("dateCreated", deltaTime)
        }
        lt("intervalStart", low)
        eq("type", type)
        household {
          eq("id", householdId())
        }
        if (interval == "weekly") order("dayOfWeek", "asc")
        else if (interval == "monthly") order("dayOfMonth", "asc")
        order("intervalStartTime", "asc")
        projections {
          if (interval == "weekly") groupProperty("dayOfWeek")
          else if (interval == "monthly") groupProperty("dayOfMonth")
          groupProperty("intervalStartTime")
          groupProperty("intervalEndTime")
          avg("avgPowerPhase1")
          avg("avgPowerPhase2")
          avg("avgPowerPhase3")
        }
      }

      def formattedAverageConsumptions = averageConsumptions.collect {
        // Merge intervalStart with intervalStartTime
        LocalTime intervalStart = null
        LocalTime intervalEnd = null
        DateTime mergedStartDate = null
        DateTime mergedEndDate = null
        int projectionIndex = 0

        switch (interval) {
          case "daily":
            intervalStart = (LocalTime) it[projectionIndex++]
            intervalEnd = (LocalTime) it[projectionIndex++]
            mergedStartDate = low.withTime(intervalStart.hourOfDay, intervalStart.minuteOfHour, intervalStart.secondOfMinute, intervalStart.millisOfSecond)
            mergedEndDate = low.withTime(intervalEnd.hourOfDay, intervalEnd.minuteOfHour, intervalEnd.secondOfMinute, intervalEnd.millisOfSecond)
            break
          case "weekly":
            int dayOfWeek = (int) it[projectionIndex++]
            intervalStart = (LocalTime) it[projectionIndex++]
            intervalEnd = (LocalTime) it[projectionIndex++]
            mergedStartDate = low.plusDays(dayOfWeek - 1).withTime(intervalStart.hourOfDay, intervalStart.minuteOfHour, intervalStart.secondOfMinute, intervalStart.millisOfSecond)
            mergedEndDate = low.plusDays(dayOfWeek - 1).withTime(intervalEnd.hourOfDay, intervalEnd.minuteOfHour, intervalEnd.secondOfMinute, intervalEnd.millisOfSecond)
            break
          case "monthly":
            int daysInMonth = low.dayOfMonth().maximumValue
            int dayOfMonth = (int) it[projectionIndex++]

            // In 30 day months, ignore 31st day
            if (dayOfMonth > daysInMonth) {
              return
            }

            intervalStart = (LocalTime) it[projectionIndex++]
            intervalEnd = (LocalTime) it[projectionIndex++]
            mergedStartDate = low.withDayOfMonth(dayOfMonth).withTime(intervalStart.hourOfDay, intervalStart.minuteOfHour, intervalStart.secondOfMinute, intervalStart.millisOfSecond)
            mergedEndDate = low.withDayOfMonth(dayOfMonth).withTime(intervalEnd.hourOfDay, intervalEnd.minuteOfHour, intervalEnd.secondOfMinute, intervalEnd.millisOfSecond)
            break
          default:
            break
        }

        // Format data
        BigDecimal avgPowerPhase1 = (BigDecimal) it[projectionIndex++]
        BigDecimal avgPowerPhase2 = (BigDecimal) it[projectionIndex++]
        BigDecimal avgPowerPhase3 = (BigDecimal) it[projectionIndex++]
        BigDecimal phasesSum = avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3
        phasesSum = phasesSum.setScale(3, RoundingMode.HALF_UP)

        def formattedDate = DateUtils.formatDateTime(mergedStartDate, mergedEndDate, false)

        // Highcharts data point object
        ["name": formattedDate, "x": mergedStartDate.getMillis(), "y": phasesSum]
      }

      dataMap["averageData"] = formattedAverageConsumptions
    }

    dataMap
  }

  def getLiveData(int numberOfValues, DateTime deltaTime) {
    // Create data for json
    def dataMap = [:]

    // Always return current server time
    dataMap["serverTime"] = new DateTime().getMillis()

    if (deltaTime) {
      dataMap["isDelta"] = true
    }

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
    dataMap.putAll(splitConsumptionsIntoPhases(consumptions))

    // Battery level from last consumption
    if (consumptions.size() > 0) {
      def consumption = consumptions.last()
      def maxBatteryLevel = 3200.0

      BigDecimal batteryLevel = new BigDecimal((Double) consumption[4])
      batteryLevel /= maxBatteryLevel
      batteryLevel = batteryLevel.min(1.0).max(0.0)
      batteryLevel = batteryLevel.setScale(2, RoundingMode.HALF_UP)
      dataMap["batteryLevel"] = batteryLevel
    }

    // Power level
    dataMap.putAll(getPowerLevel(dataMap))

    dataMap
  }

  // Helper functions
  def householdId() {
    springSecurityService.currentUser?.id
  }

  def splitConsumptionsIntoPhases(def consumptions, boolean includeXAxis = false, boolean includeEndTime = false) {
    def phase1Data = []
    def phase2Data = []
    def phase3Data = []

    consumptions.each { consumption ->
      int idx = 0
      DateTime intervalStart = (DateTime) consumption[idx++]

      DateTime intervalEnd = null
      if (includeEndTime) {
        intervalEnd = (DateTime) consumption[idx++]
      }

      // Format data
      BigDecimal powerPhase1 = new BigDecimal((Double) consumption[idx++])
      powerPhase1 = powerPhase1.setScale(3, RoundingMode.HALF_UP)

      BigDecimal powerPhase2 = new BigDecimal((Double) consumption[idx++])
      powerPhase2 = powerPhase2.setScale(3, RoundingMode.HALF_UP)

      BigDecimal powerPhase3 = new BigDecimal((Double) consumption[idx++])
      powerPhase3 = powerPhase3.setScale(3, RoundingMode.HALF_UP)

      def formattedDate = DateUtils.formatDateTime(intervalStart, includeEndTime ? intervalEnd : null);

      // Highcharts data point objects
      if (includeXAxis) {
        phase1Data << ["name": formattedDate, "x": intervalStart.getMillis(), "y": powerPhase1]
        phase2Data << ["name": formattedDate, "x": intervalStart.getMillis(), "y": powerPhase2]
        phase3Data << ["name": formattedDate, "x": intervalStart.getMillis(), "y": powerPhase3]
      } else {
        phase1Data << ["name": formattedDate, "y": powerPhase1]
        phase2Data << ["name": formattedDate, "y": powerPhase2]
        phase3Data << ["name": formattedDate, "y": powerPhase3]
      }
    }

    def dataMap = [:]
    dataMap["phase1Data"] = phase1Data
    dataMap["phase2Data"] = phase2Data
    dataMap["phase3Data"] = phase3Data
    dataMap
  }

  def formatConsumptions(def consumptions) {
    consumptions.collect {
      int idx = 0
      DateTime intervalStart = (DateTime) it[idx++]
      DateTime intervalEnd = (DateTime) it[idx++]
      BigDecimal avgPowerPhase1 = (BigDecimal) it[idx++]
      BigDecimal avgPowerPhase2 = (BigDecimal) it[idx++]
      BigDecimal avgPowerPhase3 = (BigDecimal) it[idx++]
      BigDecimal phasesSum = avgPowerPhase1 + avgPowerPhase2 + avgPowerPhase3
      phasesSum = phasesSum.setScale(3, RoundingMode.HALF_UP)

      def formattedDate = DateUtils.formatDateTime(intervalStart, intervalEnd)

      // Highcharts data point object
      ["name": formattedDate, "x": intervalStart.getMillis(), "y": phasesSum]
    }
  }

  def getPowerLevel(def collection) {
    def dataMap = [:]
    def mostRecentConsumption = 0.0
    def mostRecentConsumptionFound = false

    // If collection is an array, use last value
    if (collection instanceof ArrayList) {
      if (collection?.size()) {
        mostRecentConsumption = collection.last().y
        mostRecentConsumptionFound = true
      }
    } else if (collection instanceof Map) { // use sum of values
      // Power level
      if (collection["phase1Data"]?.size() && collection["phase2Data"]?.size() && collection["phase3Data"]?.size()) {
        mostRecentConsumption = collection["phase1Data"].last().y + collection["phase2Data"].last().y + collection["phase3Data"].last().y
        mostRecentConsumptionFound = true
      }
    }

    if (mostRecentConsumptionFound) {
      def referenceValue = Household.findById(householdId())?.referenceConsumptionValue
      def powerLevel = (referenceValue > 0) ? mostRecentConsumption / referenceValue : 0.0
      powerLevel = powerLevel.max(0.0)
      powerLevel = powerLevel.setScale(2, RoundingMode.HALF_UP)
      dataMap["powerLevel"] = powerLevel
    }

    dataMap
  }

}
