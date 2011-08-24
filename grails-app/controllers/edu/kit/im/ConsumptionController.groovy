package edu.kit.im

class ConsumptionController {

    def springSecurityService

    def scaffold = true

    def overview = {
        Household h = Household.get(springSecurityService.principal.id)

        List<Consumption> consumptions = h.consumptions as List
        consumptions = consumptions.sort { a, b -> a.timestamp.compareTo(b.timestamp) }
        List formattedConsumptions = []

        consumptions.eachWithIndex { c,i->
            formattedConsumptions.add([i, c.powerReal])
        }

        return [data: formattedConsumptions]
    }
}
