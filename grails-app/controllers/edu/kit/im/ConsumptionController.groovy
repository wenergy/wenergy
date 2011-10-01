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

import grails.converters.JSON

class ConsumptionController {

  def springSecurityService

  def scaffold = true

  def overview() {
    Household h = Household.get(springSecurityService.principal.id)

    List<Consumption> consumptions = h.consumptions
    consumptions = consumptions.sort { a, b -> a.timestamp.compareTo(b.timestamp) }
    List formattedConsumptions = []

    consumptions.eachWithIndex { c, i ->
      formattedConsumptions.add([i, c.powerReal])
    }

    int numberOfLastElements = 100
    def lastElements
    if (formattedConsumptions.size() > numberOfLastElements) {
      lastElements = formattedConsumptions.subList(formattedConsumptions.size() - numberOfLastElements, formattedConsumptions.size())
    } else {
      lastElements = formattedConsumptions
    }
    return [data: lastElements]
  }


  def dailyData() {
    Household h = Household.get(springSecurityService.principal.id)

    Date start = new Date()
    Date end = new Date()

    start.setHours(0)
    start.setMinutes(0)
    start.setSeconds(0)

    List<Consumption> consumptions = Consumption.findAllByMacAddressAndDateCreatedBetween(h.macAddress, start, end, [sort: "dateCreated", order: "asc"])
    List<BigDecimal> consumptionRealPower = consumptions.collect {it.powerReal}

    return [data: consumptionRealPower] as JSON
  }
}
