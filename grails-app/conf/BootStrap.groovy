import edu.kit.im.Role
import edu.kit.im.Household
import edu.kit.im.Peergroup
import edu.kit.im.HouseholdRole

/*
* Copyright 2011 Institute of Information Engineering and Management,
* Information & Market Engineering
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

class BootStrap {

  def springSecurityService

  def init = { servletContext ->
    // Define security roles
    def userRole = Role.findOrSaveByAuthority("ROLE_USER")
    def adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")

    // Define temporary debug household
    def macAddress = "de:ad:be:ef:fe:ed"
    def household = Household.findByMacAddress(macAddress)

    // Don't create household again on tomcat reboot or redeploy
    if (!household) {
      Peergroup peergroup = Peergroup.findOrSaveByName("homies")

      household = new Household(macAddress: macAddress, fullName: "Test User",
          eMail: "arduino@kit.edu", address: "street", zipCode: "76128", city: "KA",
          username: "test", password: springSecurityService.encodePassword('pass'), enabled: true)

      household.addToPeergroups(peergroup)
      household.save()

      // Add test user as admin and user
      if (!household.authorities.contains(adminRole)) {
        HouseholdRole.create(household, adminRole)
      }
      if (!household.authorities.contains(userRole)) {
        HouseholdRole.create(household, userRole)
      }
    }
  }
  def destroy = {
  }
}
