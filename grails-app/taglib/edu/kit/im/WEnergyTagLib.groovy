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
}
