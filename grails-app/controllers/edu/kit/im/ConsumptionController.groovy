package edu.kit.im

import grails.converters.JSON

class ConsumptionController {

  def springSecurityService

  def scaffold = true

  def overview = {
    Household h = Household.get(springSecurityService.principal.id)

    List<Consumption> consumptions = h.consumptions
    consumptions = consumptions.sort { a, b -> a.timestamp.compareTo(b.timestamp) }
    List formattedConsumptions = []

    consumptions.eachWithIndex { c, i ->
      formattedConsumptions.add([i, c.powerReal])
    }

      int numberOfLastElements = 100
      def lastElements
      if (formattedConsumptions.size() > numberOfLastElements) {
      lastElements = formattedConsumptions.subList(formattedConsumptions.size()-numberOfLastElements, formattedConsumptions.size())
    } else {
      lastElements = formattedConsumptions
    }
    return [data: lastElements]
  }


    def dailyData = {
        Household h = Household.get(springSecurityService.principal.id)

        Date start = new Date ()
        Date end = new Date ()

        start.setHours(0)
        start.setMinutes(0)
        start.setSeconds(0)

        List<Consumption> consumptions = Consumption.findAllByMacAddressAndDateCreatedBetween(h.macAddress, start, end, [sort: "dateCreated", order: "asc"])
        List<BigDecimal> consumptionRealPower = consumptions.collect {it.powerReal}

        return [data: consumptionRealPower] as JSON
    }
}
