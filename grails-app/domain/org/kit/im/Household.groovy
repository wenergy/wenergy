package org.kit.im

class Household {

    String firstname
    String lastname

    String email

    String address

    BigDecimal meterID

    static hasMany = [appliances : Appliance, peergroups : Peergroup, consumptions : Consumption]


    static constraints = {
        firstname(nullable: false)
        lastname(nullable: false)
        email(nullable: false)
        address(nullable: false)
        meterID(nullable: false)
        username blank: false, unique: true
		password blank: false
    }

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
