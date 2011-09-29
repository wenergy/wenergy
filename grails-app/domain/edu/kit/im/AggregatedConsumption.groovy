package edu.kit.im


class AggregatedConsumption {

  String macAddress

  ConsumptionType type

  Date intervalBegin
  Date intervalEnd

  BigDecimal sumPowerReal
  BigDecimal avgPowerReal

  static hasMany = [consumptions: Consumption]

  Date dateCreated

    static constraints = {
    }


}
