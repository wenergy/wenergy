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

package edu.kit.im

import org.joda.time.DateTime

class AdminController {

  def sessionFactory
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
  def apiService

  def index() {

    if (params.username?.size() && params.submit != null) {

      def userRole = Role.findByAuthority("ROLE_USER")
      def user = Household.findByUsername(params.username)

      if (!user) {
        flash.message = "User not found"
      } else {

        if (params.submit == "Allow") {

          if (user.authorities.contains(userRole)) {
            flash.message = "User already allowed"
          } else {
            HouseholdRole.create(user, userRole)
            flash.message = "User added to allowed group"
          }

        } else { // Deny

          if (!user.authorities.contains(userRole)) {
            flash.message = "User is not allowed anyways"
          } else {
            HouseholdRole.remove(user, userRole)
            flash.message = "User removed from allowed group"
          }

        }
      }
    }
  }

  def debug() {
    // Initial data already loaded
    if (Consumption.count()) {
      render "error"
      return
    }
    // Create some data
    def macAddress = "de:ad:be:ef:fe:ed"
    def household = Household.findByMacAddress(macAddress)
    def startDate = new DateTime(2011, 9, 1, 0, 0, 0)
    def endDate = new DateTime().withTimeAtStartOfDay().plusHours(12)
    def baseLoad = 0 //kWh
    def i = 0
    while (++i) {
      def power = baseLoad + ((startDate.weekOfWeekyear % 2 == 0) ? 2 : 4)
      def consumption = new Consumption(household: household, date: startDate, powerPhase1: power, powerPhase2: 0, powerPhase3: 0);
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

    render "ok"
  }

  def cleanUpGorm() {
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }
}
