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

class Consumption {
  // Arduino MAC Address
  String macAddress

  // Date and time at which the consumption data was collected
  DateTime timestamp

  // Power values
  BigDecimal powerReal
  BigDecimal powerReactive

  // Grails information
  DateTime dateCreated

  // Relationships
  static belongsTo = Household

  static constraints = {
    macAddress(blank: false, matches: "([0-9a-f]{2}[:]){5}([0-9a-f]{2})")
    timestamp(nullable: false)
    powerReal(nullable: false, scale: 3)
    powerReactive(nullable: false, scale: 3)
    dateCreated()
  }
}
