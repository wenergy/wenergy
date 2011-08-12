package org.kit.im

class Consumption {

    Date timestamp
    BigDecimal amountOfEnergy

    static belongsTo = Household


    static constraints = {
        timestamp(nullable: false)
        amountOfEnergy(nullable: false)
    }
}
