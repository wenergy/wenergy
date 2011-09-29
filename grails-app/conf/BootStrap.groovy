import edu.kit.im.Consumption
import edu.kit.im.Household
import edu.kit.im.Peergroup
import edu.kit.im.HouseholdRole
import edu.kit.im.Role

class BootStrap {

  def springSecurityService

  def init = { servletContext ->
    def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
    def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

    def macAddress = "de:ad:be:ef:fe:ed"
    Household household = Household.findByMacAddress(macAddress)
    // Don't create household again on tomcat reboot or redeploy
    if (!household) {
      Peergroup peergroup = Peergroup.findByName("homies") ?: new Peergroup(name: "homies").save(failOnError: true)
      household = new Household(macAddress: macAddress, firstName: "arduino1", lastName: "dalen", eMail: "dalen@kit.edu", address: "address",
          username: "id1", password: springSecurityService.encodePassword('password'), enabled: true)
      household.addToPeergroups(peergroup)
      household.save()
      if (!household.authorities.contains(adminRole)) {
        HouseholdRole.create household, adminRole
      }
    }

/*        int i = 1
        Peergroup peergroup = new Peergroup(name: "homies")
        peergroup.save()
        2.times {

            Household h = new Household(enabled: true, firstName: "firstName"+i, lastName: "lastName"+i, username: "id"+i, eMail: "$i@kit.edu", password: springSecurityService.encodePassword('password'), address: "address"+i, macAddress: i)

            h.addToPeergroups(peergroup)
            h.save()

            if (!h.authorities.contains(adminRole)) {
	            HouseholdRole.create h, adminRole
	        }


            i++
        }*/

    /* Debug consumptions */
//    int j = 0
    //    150.times {
    //
    //      Consumption consumption = new Consumption(macAddress: macAddress, powerReactive: new BigDecimal(j), powerReal: new BigDecimal(j), timestamp: new Date(j as long))
    //      household.addToConsumptions(consumption)
    //
    //      j++
    //    }
    //    household.save()

  }
  def destroy = {
  }
}
