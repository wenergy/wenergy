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
        out << household.fullName
      }
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
    Household household = springSecurityService.currentUser
    if (household && household?.theme != ThemeType.wenergy) {
      out << r.require([modules: household?.theme?.key])
    }
  }

  def themeLogo = { attrs, body ->
    Household household = springSecurityService.currentUser
    def key = ThemeType.wenergy.key
    if (household) {
      key = household.theme.key
    }
    out << r.img([dir: "images", file: "${key}.png"])
  }

  def isTheme = { attrs, body ->
    Household household = springSecurityService.currentUser
    def theme = ThemeType.wenergy
    if (household) {
      theme = household.theme
    }
    if (theme == ThemeType.valueOf(attrs.key)) {
      out << body()
    }
  }
}
