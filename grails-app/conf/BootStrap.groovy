import org.kit.im.Appliance
import org.kit.im.Consumption
import org.kit.im.Household
import org.kit.im.Peergroup
import org.kit.im.HouseholdRole
import grails.plugins.springsecurity.SpringSecurityService
import org.kit.im.Role

class BootStrap {

    def springSecurityService


    def init = { servletContext ->
        def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
        def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

        Peergroup peergroup = new Peergroup(groupName: "homies")
        peergroup.save()
        Household h = new Household(enabled: true, firstname: "arduino1", lastname: "dalen", username: "id1", email: "dalen@kit.edu", password: springSecurityService.encodePassword('password'), address: "address", meterID: "de:ad:be:ef:fe:ed")
        h.addToPeergroups(peergroup)
        h.save()
        if (!h.authorities.contains(adminRole))
         {
	            HouseholdRole.create h, adminRole
	     }

/*        int i = 1
        Peergroup peergroup = new Peergroup(groupName: "homies")
        peergroup.save()
        2.times {

            Household h = new Household(enabled: true, firstname: "firstname"+i, lastname: "lastname"+i, username: "id"+i, email: "$i@kit.edu", password: springSecurityService.encodePassword('password'), address: "address"+i, meterID: i)

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
