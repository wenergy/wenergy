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



        int i = 1
        Peergroup peergroup = new Peergroup(groupName: "homies")
        peergroup.save()
        5.times {

            Household h = new Household(enabled: true, firstname: "firstname"+i, lastname: "lastname"+i, username: "id"+i, email: "$i@kit.edu", password: springSecurityService.encodePassword('password'), address: "address"+i, meterID: i)

            h.addToPeergroups(peergroup)


            100.times {
                Consumption c = new Consumption(timestamp: new Date(), amountOfEnergy: Math.random())
                c.save()
                h.addToConsumptions(c)
            }
            h.save()

            if (!h.authorities.contains(adminRole)) {
	            HouseholdRole.create h, adminRole
	        }


            i++
        }



    }
    def destroy = {
    }
}
