import edu.kit.im.Appliance
import edu.kit.im.Consumption
import edu.kit.im.Household
import edu.kit.im.Peergroup
import edu.kit.im.HouseholdRole
import grails.plugins.springsecurity.SpringSecurityService
import edu.kit.im.Role

class BootStrap {

    def springSecurityService


    def init = { servletContext ->
        def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
        def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

        Peergroup peergroup = new Peergroup(name: "homies")
        peergroup.save()
        Household h = new Household(enabled: true, firstName: "arduino1", lastName: "dalen", username: "id1", eMail: "dalen@kit.edu", password: springSecurityService.encodePassword('password'), address: "address", macAddress: "de:ad:be:ef:fe:ed")
        h.addToPeergroups(peergroup)
        h.save()
        if (!h.authorities.contains(adminRole))
         {
	            HouseholdRole.create h, adminRole
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



    }
    def destroy = {
    }
}
