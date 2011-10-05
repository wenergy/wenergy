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
import org.joda.time.Period

class DateService {

  static transactional = false

  // Round down to lower minutes
  def floorDateMinutes(DateTime date, int minutes) {
    // Special treatment for hours
    if (minutes > 59 && minutes % 60 == 0) {
      // Convert minutes to hours
      minutes = Period.minutes(minutes).toStandardHours().hours
      return floorDateHours(date, minutes)
    }
    int unroundedMinutes = date.minuteOfHour
    int mod = unroundedMinutes % minutes
    DateTime newDate = date.minusMinutes(mod)
    // Clear seconds and return new date
    newDate.withSecondOfMinute(0)
  }

  // Round up to upper minutes
  def ceilDateMinutes(DateTime date, int minutes) {
    // Special treatment for hours
    if (minutes > 59 && minutes % 60 == 0) {
      // Convert minutes to hours
      minutes = Period.minutes(minutes).toStandardHours().hours
      return ceilDateHours(date, minutes)
    }
    int unroundedMinutes = date.minuteOfHour
    int mod = unroundedMinutes % minutes
    DateTime newDate = date.plusMinutes((mod == 0) ? minutes : minutes - mod)
    // Clear seconds and return new date
    newDate.withSecondOfMinute(0)
  }

  // Round down to lower hours
  def floorDateHours(DateTime date, int hours) {
    int unroundedHours = date.hourOfDay
    int mod = unroundedHours % hours
    DateTime newDate = date.minusHours(mod)
    // Clear minutes and seconds and return new date
    newDate = newDate.withMinuteOfHour(0)
    newDate.withSecondOfMinute(0)
  }

  // Round up to upper hours
  def ceilDateHours(DateTime date, int hours) {
    int unroundedHours = date.hourOfDay
    int mod = unroundedHours % hours
    DateTime newDate = date.plusHours((mod == 0) ? hours : (hours - mod))
    // Clear minutes and seconds and return new date
    newDate = newDate.withMinuteOfHour(0)
    newDate.withSecondOfMinute(0)
  }
}
