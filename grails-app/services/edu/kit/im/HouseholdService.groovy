package edu.kit.im

import java.math.RoundingMode
import org.joda.time.DateTime

class HouseholdService {
  def run() {
    // Last two days of consumption records are used to determine reference level
    def referenceTime = new DateTime().minusDays(2)
    Household.getAll().each {h ->
      // Collect consumptions for every household
      def consumptions = Consumption.withCriteria() {
        ge("date", referenceTime)
        household {
          eq("id", h.id)
        }
        projections {
          property("powerPhase1")
          property("powerPhase2")
          property("powerPhase3")
        }
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

      try {
        // Get quantile
        def numberOfConsumptions = aggregatedConsumptions.size()
        def index = (0.9 * numberOfConsumptions) as Integer
        if (index < aggregatedConsumptions.size()) {
          h.referenceConsumptionValue = aggregatedConsumptions.get(index)
          h.save()
        }
      } catch (Exception e) {
        log.error e
      }
    }
  }
}
