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

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import edu.kit.im.enums.ThemeType

class WEnergyTagLib {

  static namespace = "wen"

  def springSecurityService

  // Full name displayed for logged in user
  def fullName = {
    if (springSecurityService.isLoggedIn()) {
      Household user = springSecurityService.currentUser
      out << user.fullName.encodeAsHTML()
    }
  }

  // Resolve id to user
  def fullNameForId = { attrs, body ->
    Long id = attrs.householdId as Long
    if (id) {
      Household household = Household.findById(id)
      if (household) {
        out << g.link(controller: "household", action: "show", id: household.id) { household.fullName }
      }
    }
  }

  // Get user id
  def householdId = {
    if (springSecurityService.isLoggedIn()) {
      Household user = springSecurityService.currentUser
      out << user.id
    } else {
      out << "-1"
    }
  }

  // Security additions for individual user queries
  def ifGrantedForUser = { attrs, body ->
    def authorities = Household.findById(attrs.user)?.getAuthorities()?.collect { it.authority }

    if (authorities?.contains(attrs.role)) {
      out << body()
    }
  }

  def ifNotGrantedForUser = { attrs, body ->
    def authorities = Household.findById(attrs.user)?.getAuthorities()?.collect { it.authority }

    if (!authorities?.contains(attrs.role)) {
      out << body()
    }
  }

  // User switching support
  def switchedUserOriginalUsername = { attrs, body ->
    if (SpringSecurityUtils.isSwitched()) {
      def username = SpringSecurityUtils.switchedUserOriginalUsername
      def household = Household.findByUsername(username)
      out << household?.fullName?.encodeAsHTML()
    }
  }

  // Theming
  def themeResources = { attrs, body ->
    if (springSecurityService.isLoggedIn()) {
      Household household = springSecurityService.currentUser
      if (household) {
        out << r.require([modules: household?.theme?.key])
      }
    }
  }

  def themeLogo = { attrs, body ->
    def key = ThemeType.wenergy.key
    if (springSecurityService.isLoggedIn()) {
      Household household = springSecurityService.currentUser
      if (household) {
        key = household.theme.key
      }
    }
    out << r.img([dir: "images", file: "${key}.png"])
  }

  def isTheme = { attrs, body ->
    def theme = ThemeType.wenergy
    if (springSecurityService.isLoggedIn()) {
      Household household = springSecurityService.currentUser
      if (household) {
        theme = household.theme
      }
    }
    if (theme == ThemeType.valueOf(attrs.key)) {
      out << body()
    }
  }

  // Sorting
  def sortableDate = { attrs, body ->
    DateTime date = attrs.date
    if (date) {
      DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYYMMddHHmmss").withLocale(Locale.GERMAN)
      out << formatter.print(date)
    }
  }

  // Chart exporing
  def exportingEnabled = { attrs, body ->
    if (springSecurityService.isLoggedIn()) {
      Household household = springSecurityService.currentUser
      def authorities = household?.getAuthorities()?.collect { it.authority }
      if (SpringSecurityUtils.isSwitched() || authorities?.contains("ROLE_ADMIN")) {
        out << "true"
      } else {
        out << "false"
      }
    } else {
      out << "false"
    }
  }
}
