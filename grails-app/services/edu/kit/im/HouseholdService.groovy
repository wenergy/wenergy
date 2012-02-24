package edu.kit.im

import java.math.RoundingMode
import org.joda.time.DateTime

class HouseholdService {
  def run() {
    def referenceTime = new DateTime().minusDays(2) //last two days of consumption records are used to determine reference level
    Household.getAll().each {h ->
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
        // Format data
        BigDecimal PowerPhase1 = new BigDecimal((Double) it[0])
        PowerPhase1.setScale(3, RoundingMode.HALF_UP)

        BigDecimal PowerPhase2 = new BigDecimal((Double) it[1])
        PowerPhase2.setScale(3, RoundingMode.HALF_UP)

        BigDecimal PowerPhase3 = new BigDecimal((Double) it[2])
        PowerPhase3.setScale(3, RoundingMode.HALF_UP)

        // Format data as [timestamp, powerValue]
        new BigDecimal(PowerPhase1 + PowerPhase2 + PowerPhase3)
      }
      Collections.sort(aggregatedConsumptions)
      try {
        def numberOfConsumptions = aggregatedConsumptions.size()
        def index = (0.9 * numberOfConsumptions) as Integer
        h.referenceConsumption = aggregatedConsumptions.get(index)
        h.save()
      }
      catch (Exception e) {
        log.error(e)
      }
    }
  }
}
