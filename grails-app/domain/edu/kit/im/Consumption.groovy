package edu.kit.im

class Consumption {

    String idMac
    Date timestamp
    BigDecimal powerReal
    BigDecimal powerReactive

    static belongsTo = Household


    static constraints = {
        timestamp(nullable: false)
        powerReal(nullable: false)
        powerReactive(nullable: false)
        idMac(nullable: false)
    }
}
