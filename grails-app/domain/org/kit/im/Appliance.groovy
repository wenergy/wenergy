package org.kit.im

class Appliance {

    String applianceName
    BigDecimal deltaRealPower
    BigDecimal deltaReactivePower

    static belongsTo = Household

    static constraints = {
        applianceName(nullable: false)
        deltaRealPower(nullable: false)
        deltaReactivePower(nullable: false)
    }
}
