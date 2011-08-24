package edu.kit.im

class Household {
  // Adriuno MAC Address
  String macAddress

  String firstName
  String lastName
  String eMail
  String address
  Date dateCreated

  // default type is Set but we need List
  List consumptions

  static hasMany = [appliances: Appliance, peergroups: Peergroup, consumptions: Consumption]

  static constraints = {
    macAddress(nullable: false)
    firstName(nullable: false)
    lastName(nullable: false)
    eMail(nullable: false)
    address(nullable: false)
    username(blank: false, unique: true)
    password(blank: false)
    dateCreated()
  }

  // Spring Security variables
  String username
  String password
  boolean enabled
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired

  static mapping = {
    password column: '`password`'
  }

  Set<Role> getAuthorities() {
    HouseholdRole.findAllByHousehold(this).collect { it.role } as Set
  }
}
