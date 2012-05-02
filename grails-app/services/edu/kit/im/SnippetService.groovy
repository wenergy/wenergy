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

package edu.kit.im

//import org.joda.time.DateTime
//import org.joda.time.DateTimeZone

class SnippetService {

  def deleteConsumptionsForHouseholdOlderThan(def household, def date) {
//    def date = new DateTime(2012, 04, 19, 18, 0, 0, DateTimeZone.forID("Europe/Berlin"))
    def aggCon = AggregatedConsumption.findAllByHouseholdAndIntervalStartLessThanEquals(household, date)
    aggCon?.each { ac ->
      ac.consumptions.clear()
      ac.delete(flush: true)
    }

    def cons = Consumption.findAllByHouseholdAndDateLessThanEquals(household, date)
    cons.each { con ->
      household.removeFromConsumptions(con)
      con.delete()
      household.save(flush: true)
    }
  }

  def deleteInstancesOfDomainClass() {
    ApiError.executeUpdate("delete from ApiError")
  }

  def getLastConsumptionOfHousehold() {
    def household = Household.findById(1)
    def consumption = Consumption.findByHousehold(household, [sort: "date", order: "desc", max: 1])
  }

  def executeService() {
    def service = ctx.getBean("myService")
    service?.method()
  }
}
