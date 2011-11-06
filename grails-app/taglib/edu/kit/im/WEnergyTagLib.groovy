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

class WEnergyTagLib {

  static namespace = "wen"

  def springSecurityService

  // Brand
  def brand = {
    out << '<span class="wenergy-brand">w<span class="wenergy-e">e</span>nergy</span>'
  }

  // Full name displayed for logged in user
  def fullName = {
    if (springSecurityService.isLoggedIn()) {
      Household user = springSecurityService.currentUser
      out << user.fullName.encodeAsHTML()
    }
  }

  // System status
  def systemStatus = {
    def currentOut = out
    def statusMap = [:]

    statusMap["Households"] = Household.count()
    statusMap["Peer groups"] = Peergroup.count()
    statusMap["Consumption records"] = Consumption.count()
    statusMap["Aggregated consumption records"] = AggregatedConsumption.count()

    statusMap.each {
      out << "<tr>"
      out << "<td>" + it.key.encodeAsHTML() + "</td>"
      out << "<td>" + it.value.encodeAsHTML() + "</td>"
      out << "</tr>"
    }
  }

}