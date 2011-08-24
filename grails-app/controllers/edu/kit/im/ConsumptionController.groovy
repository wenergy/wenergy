package edu.kit.im

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

    def lastElements
    int numberOfLastElements = 100
    if (formattedConsumptions.size() > numberOfLastElements) {
      lastElements = formattedConsumptions.subList(formattedConsumptions.size()-numberOfLastElements, formattedConsumptions.size())
    } else {
      lastElements = formattedConsumptions
    }
    return [data: lastElements]
  }
}
