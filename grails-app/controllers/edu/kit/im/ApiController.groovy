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

class ApiController {

  //def allowedMethods = [saveConsumption: "POST"]

  def saveConsumption() {

    if (request.method == "GET") {
      render("invalid call")
      return
    }

    def json = request.JSON
    def household
    household = Household.findByMacAddress(json?.id)

    Date date = new Date((json?.t as long) * 1000.0 as long)

    // TODO arduino shall send timestamps in millis (*1000)
    Consumption consumption = new Consumption(macAddress: json?.id, powerReactive: new BigDecimal(json?.q), powerReal: new BigDecimal(json?.p), timestamp: date)
    consumption.save()
    household.addToConsumptions(consumption)

    List<AggregatedConsumption> aclist = AggregatedConsumption.withCriteria {
      ge("intervalBegin", date)
      lt("intervalEnd", date)
      eq("type", ConsumptionType.MIN5)
      eq("macAddress", consumption.macAddress)
      maxResults(1)
    }
    if (aclist.size() == 1) {
      AggregatedConsumption ac = aclist.get(0)
      ac.addToConsumptions(consumption)
      ac.sumPowerReal += consumption.powerReal
      ac.avgPowerReal = ac.sumPowerReal / ac.consumptions.size()
      ac.save()
    }
    else {
      Date intervalBegin = date
      Date intervalEnd = date
      intervalBegin.setMinutes((int) (Math.floor(intervalBegin.minutes / 5) * 5))
      intervalEnd.setMinutes((int) (Math.floor((intervalEnd.minutes + 5) / 5) * 5))
      intervalBegin.setSeconds(0)
      intervalEnd.setSeconds(0)
      AggregatedConsumption ac = new AggregatedConsumption(type: ConsumptionType.MIN5, intervalBegin: intervalBegin, intervalEnd: intervalEnd, macAddress: consumption.macAddress)
      ac.sumPowerReal = consumption.powerReal
      ac.avgPowerReal = consumption.powerReal // / 1
      ac.save()
    }

    // id t p q
    render(json?.id + " " + json?.t + " " + json?.p)
  }
}
