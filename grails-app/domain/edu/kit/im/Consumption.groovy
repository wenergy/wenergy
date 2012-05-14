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

import org.apache.commons.lang.builder.ToStringBuilder
import org.joda.time.DateTime

class Consumption implements Serializable {
  // Date and time at which the consumption data was collected
  DateTime date

  // Reactive power values
  BigDecimal powerPhase1
  BigDecimal powerPhase2
  BigDecimal powerPhase3

  // Battery statistic
  BigDecimal batteryLevel

  // Grails information
  DateTime dateCreated

  // Relationships
  static belongsTo = [household: Household]

  static constraints = {
    date(nullable: false)
    powerPhase1(nullable: false, min: 0.0, scale: 3)
    powerPhase2(nullable: false, min: 0.0, scale: 3)
    powerPhase3(nullable: false, min: 0.0, scale: 3)
    batteryLevel(nullable: false, min: 0.0, scale: 3)
    household(bindable: true)
    dateCreated()
  }

  static mapping = {
    date(index: "date_idx")
    dateCreated(index: "date_created_index")
    cache(true)
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).
        append("id", id).
        append("date", date).
        append("powerPhase1", powerPhase1).
        append("powerPhase2", powerPhase2).
        append("powerPhase3", powerPhase3).
        append("batteryLevel", batteryLevel).
        append("dateCreated", dateCreated).
        append("version", version).
        toString();
  }
}
