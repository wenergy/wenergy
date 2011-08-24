package edu.kit.im

class Appliance {

  String name
  BigDecimal deltaRealPower
  BigDecimal deltaReactivePower
  Date dateCreated

  static belongsTo = Household

  static constraints = {
    name(nullable: false)
    deltaRealPower(nullable: false)
    deltaReactivePower(nullable: false)
    dateCreated()
  }
}
