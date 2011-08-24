package edu.kit.im

class Consumption {

  String macAddress
  Date timestamp
  BigDecimal powerReal
  BigDecimal powerReactive
  Date dateCreated

  static belongsTo = Household

  static constraints = {
    macAddress(nullable: false)
    timestamp(nullable: false)
    powerReal(nullable: false)
    powerReactive(nullable: false)
    dateCreated()
  }
}
