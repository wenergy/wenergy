package edu.kit.im

import edu.kit.im.messages.ReferenceConsumptionMessage
import org.joda.time.DateTime

class HouseholdService {
  def run() {
    // Last two days of consumption records are used to determine reference level
    def referenceTime = new DateTime().minusDays(3)
    Household.getAll().each {h ->
      determineReferenceConsumptionValue(h.id, referenceTime)
    }
  }

  def determineReferenceConsumptionValue(Long householdId, DateTime sinceDateTime = new DateTime().minusDays(3)) {
    // Collect consumptions for every household
    def consumptions = Consumption.withCriteria() {
      ge("date", sinceDateTime)
      household {
        eq("id", householdId)
      }
      projections {
        property("powerPhase1")
        property("powerPhase2")
        property("powerPhase3")
      }
      timeout(60)
      readOnly(true)
    }

    def aggregatedConsumptions = consumptions.collect {
      BigDecimal powerPhase1 = new BigDecimal((Double) it[0])
      BigDecimal powerPhase2 = new BigDecimal((Double) it[1])
      BigDecimal powerPhase3 = new BigDecimal((Double) it[2])

      BigDecimal sum = powerPhase1 + powerPhase2 + powerPhase3
      sum
    }

    // Sort sums
    Collections.sort(aggregatedConsumptions)

    // Get quantile
    def numberOfConsumptions = aggregatedConsumptions.size()
    def index = (0.99 * numberOfConsumptions) as Integer
    if (index < aggregatedConsumptions.size()) {
      rabbitSend "wenergy", "db", new ReferenceConsumptionMessage(householdId, aggregatedConsumptions.get(index))
    }
  }
}
