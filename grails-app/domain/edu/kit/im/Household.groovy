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

class Household {
  // Arduino MAC Address
  String macAddress

  // User information
  String fullName
  String eMail
  String address
  String zipCode
  String city

  // Grails information
  DateTime dateCreated

  // Default type is Set but we need List
  List consumptions

  // Relationships
  static hasMany = [appliances: Appliance, peergroups: Peergroup, consumptions: Consumption]

  static constraints = {
    macAddress(nullable: true, unique: true, matches: "([0-9a-f]{2}[:]){5}([0-9a-f]{2})")
    fullName(blank: false)
    eMail(blank: false, unique: true, email: true)
    address(blank: false)
    zipCode(blank: false, matches: "[0-9]{5}")
    city(blank: false)
    username(blank: false, unique: true, minSize: 3)
    password(blank: false, password: true, minSize: 3)
    dateCreated()
  }

  // Spring Security variables
  String username
  String password
  boolean enabled = false
  boolean accountExpired = false
  boolean accountLocked = false
  boolean passwordExpired = false

  static mapping = {
    password column: '`password`'
  }

  Set<Role> getAuthorities() {
    HouseholdRole.findAllByHousehold(this).collect { it.role } as Set
  }
}
