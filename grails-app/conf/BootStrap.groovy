import edu.kit.im.Role
import edu.kit.im.Household
import edu.kit.im.Peergroup
import edu.kit.im.HouseholdRole
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import edu.kit.im.Consumption

/*
* Copyright 2011-2012 Institute of Information Engineering and Management,
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
  def apiService

  def init = { servletContext ->
    // Work only on UTC by default - necessary since flot uses UTC as well
    DateTimeZone.setDefault(DateTimeZone.UTC)

    // Define security roles
    def userRole = Role.findOrSaveByAuthority("ROLE_USER")
    def adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")

    // Define temporary debug household
    def deviceId = 1
    def household = Household.findByDeviceId(deviceId)

    // Don't create household again on tomcat reboot or redeploy
    if (!household) {
      Peergroup peergroup = Peergroup.findOrSaveByName("Institut")

      household = new Household(peergroup: peergroup, deviceId: deviceId, fullName: "Lehrstuhl",
          eMail: "test@iism.uni-karlsruhe.de", address: "Englerstr. 14", zipCode: "76128", city: "Karlsruhe",
          username: "admin", password: springSecurityService.encodePassword("pass"), enabled: true)

      household.save()

      // Add test user as admin and user
      if (!household.authorities.contains(adminRole)) {
        HouseholdRole.create(household, adminRole)
      }
      if (!household.authorities.contains(userRole)) {
        HouseholdRole.create(household, userRole)
      }

//      DateTime date = new DateTime()
//      100.times {
//        def c = new Consumption(household: household, date: date, powerPhase1: Math.random() * 100, powerPhase2: Math.random() * 100, powerPhase3: Math.random() * 100, batteryLevel: 0)
//        c.save()
//
//        date = date.plusMinutes(7)
//
//        apiService.determineAggregation(c)
//      }
    }
  }
  def destroy = {
  }
}
