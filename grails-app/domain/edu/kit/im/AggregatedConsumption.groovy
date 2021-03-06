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
import org.apache.commons.lang.builder.ToStringBuilder
import org.joda.time.DateTime
import org.joda.time.LocalTime

class AggregatedConsumption implements Serializable {
  // Aggregation type
  ConsumptionType type

  // Aggregation interval dates
  // Project-wide assumption is half open interval [0:00, 0:05)
  DateTime intervalStart
  DateTime intervalEnd

  // Extracted time values from intervalStart and intervalEnd for querying
  LocalTime intervalStartTime
  LocalTime intervalEndTime
  // Date values of start date, also for querying
  int dayOfWeek
  int dayOfMonth

  // Aggregated values
  BigDecimal sumPowerPhase1
  BigDecimal avgPowerPhase1

  BigDecimal sumPowerPhase2
  BigDecimal avgPowerPhase2

  BigDecimal sumPowerPhase3
  BigDecimal avgPowerPhase3

  // Relationships
  static hasMany = [consumptions: Consumption]
  static belongsTo = [household: Household]

  // Grails information
  DateTime dateCreated

  static constraints = {
    type(nullable: false)
    intervalStart(nullable: false)
    intervalEnd(nullable: false)
    intervalStartTime(nullable: false)
    intervalEndTime(nullable: false)
    dayOfWeek(range: 1..7)
    dayOfMonth(range: 1..31)
    sumPowerPhase1(nullable: false, min: 0.0, scale: 3)
    avgPowerPhase1(nullable: false, min: 0.0, scale: 3)
    sumPowerPhase2(nullable: false, min: 0.0, scale: 3)
    avgPowerPhase2(nullable: false, min: 0.0, scale: 3)
    sumPowerPhase3(nullable: false, min: 0.0, scale: 3)
    avgPowerPhase3(nullable: false, min: 0.0, scale: 3)
    consumptions(bindable: true)
    household(bindable: true)
    dateCreated()
  }

  static mapping = {
    intervalStart(index: "intervalStart_idx,intervalStart_type_idx,intervalStart_intervalEnd_type_idx")
    intervalEnd(index: "intervalStart_intervalEnd_type_idx")
    type(index: "intervalStart_type_idx,intervalStart_intervalEnd_type_idx")
    cache(true)
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).
        append("id", id).
        append("type", type).
        append("intervalStart", intervalStart).
        append("intervalEnd", intervalEnd).
        append("intervalStartTime", intervalStartTime).
        append("intervalEndTime", intervalEndTime).
        append("dayOfWeek", dayOfWeek).
        append("dayOfMonth", dayOfMonth).
        append("sumPowerPhase1", sumPowerPhase1).
        append("avgPowerPhase1", avgPowerPhase1).
        append("sumPowerPhase2", sumPowerPhase2).
        append("avgPowerPhase2", avgPowerPhase2).
        append("sumPowerPhase3", sumPowerPhase3).
        append("avgPowerPhase3", avgPowerPhase3).
        append("dateCreated", dateCreated).
        append("version", version).
        toString();
  }
}
