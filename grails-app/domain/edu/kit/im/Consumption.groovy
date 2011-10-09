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

class Consumption implements Serializable {
  // Date and time at which the consumption data was collected
  DateTime date

  // Power values
  BigDecimal powerReal
  BigDecimal powerReactive

  // Grails information
  DateTime dateCreated

  // Relationships
  static belongsTo = [household: Household]

  static constraints = {
    date(nullable: false)
    powerReal(nullable: false, scale: 3)
    powerReactive(nullable: false, scale: 3)
    dateCreated()
  }

  static mapping = {
    cache(true)
  }
}
