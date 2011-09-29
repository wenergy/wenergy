package edu.kit.im

class ApiController {

    //def allowedMethods = [saveConsumption: "POST"]

    def saveConsumption = {

        if (request.method=="GET")
        {
            render("invalid call")
            return
        }

        def json = request.JSON
        def household
        household = Household.findByMacAddress(json?.id)

        Consumption consumption= new Consumption(macAddress: json?.id,powerReactive: new BigDecimal(json?.q),powerReal: new BigDecimal(json?.p),timestamp: new Date(json?.t as long ))
        consumption.save()
        household.addToConsumptions(consumption)

        // id t p q
        render (json?.id + " " + json?.t + " " + json?.p)
    }
}
