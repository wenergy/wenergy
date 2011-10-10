import edu.kit.im.Role
import edu.kit.im.Household
import edu.kit.im.Peergroup
import edu.kit.im.HouseholdRole
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import edu.kit.im.Consumption

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
  def apiService
  def sessionFactory
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

  def init = { servletContext ->
    // Work only on UTC by default - necessary since flot uses UTC as well
    DateTimeZone.setDefault(DateTimeZone.UTC)

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

    return;

    // Debug
    // Create some data
    def startDate = new DateTime(2011, 8, 1, 0, 0, 0)
    def endDate = new DateTime().withTimeAtStartOfDay().plusHours(12)
    def baseLoad = 0 //kWh
    def i = 0
    while (++i) {
      def power = baseLoad + ((startDate.weekOfWeekyear % 2 == 0) ? 2 : 4)
      def consumption = new Consumption(household: household, date: startDate, powerReal: power, powerReactive: power);
      consumption.save(failOnError: true)

      apiService.determineAggregation(consumption)

      startDate = startDate.plusMinutes(5)

      if (i % 100 == 0) {
        log.error "${i} ${startDate}"
        cleanUpGorm()
      }

      if (startDate.isAfter(endDate)) {
        break;
      }
    }
  }
  def destroy = {
  }

  def cleanUpGorm() {
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }

}
