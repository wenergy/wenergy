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

import org.joda.time.DateTime
import org.joda.time.DateTimeConstants

class RankingService {

  def run() {
    // Last two days of consumption records are used to determine reference level
    Household.getAll().each {h ->
      Household.withNewSession { session ->
        determineRankingValue(h.id, true)
      }
    }
  }

  def determineRankingValue(Long householdId, boolean fromJob = false) {
    // Collect aggregated consumptions for every household
    def startDateTime = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
    def endDateTime

    if (fromJob) {
      endDateTime = startDateTime
      startDateTime = startDateTime.minusDays(7)
    }

    def aggregatedConsumptions = AggregatedConsumption.withCriteria() {
      if (fromJob) {
        between("intervalStart", startDateTime, endDateTime)
      } else {
        ge("intervalStart", startDateTime)
      }
      eq("type", ConsumptionType.H3)
      household {
        eq("id", householdId)
      }
      projections {
        sum("sumPowerPhase1")
        sum("sumPowerPhase2")
        sum("sumPowerPhase3")
      }
    }

    def aggregatedConsumptionSum = aggregatedConsumptions.collect {
      BigDecimal powerPhase1 = new BigDecimal((BigDecimal) it[0] ?: 0.0)
      BigDecimal powerPhase2 = new BigDecimal((BigDecimal) it[1] ?: 0.0)
      BigDecimal powerPhase3 = new BigDecimal((BigDecimal) it[2] ?: 0.0)

      BigDecimal sum = powerPhase1 + powerPhase2 + powerPhase3
      sum
    }

    if (fromJob && aggregatedConsumptionSum?.size()) {
      Household h = Household.findById(householdId)
      h.referenceRankingValue = aggregatedConsumptionSum.first()
      h.save()
    } else if (aggregatedConsumptionSum?.size()) {
      return aggregatedConsumptionSum.first()
    }

    null
  }
}
