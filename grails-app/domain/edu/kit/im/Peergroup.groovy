package edu.kit.im

class Peergroup {

  String name
  Date dateCreated

  static hasMany = [households: Household]
  static belongsTo = Household

  static constraints = {
    name(nullable: false, unique: true)
    dateCreated()
  }
}
