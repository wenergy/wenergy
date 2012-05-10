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

package edu.kit.im.utils

import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTimeConstants
import edu.kit.im.enums.ConsumptionType

class DateUtils {

  // Round down to lower minutes
  static DateTime floorDateMinutes(DateTime date, int minutes) {
    // Special treatment for hours
    if (minutes > 59 && minutes % 60 == 0) {
      // Convert minutes to hours
      minutes = Period.minutes(minutes).toStandardHours().hours
      return floorDateHours(date, minutes)
    }
    // ----------
    int unroundedMinutes = date.minuteOfHour
    int mod = unroundedMinutes % minutes
    DateTime newDate = date.minusMinutes(mod)
    // Clear seconds and return new date
    return newDate.withSecondOfMinute(0)
  }

  // Round up to upper minutes
  static DateTime ceilDateMinutes(DateTime date, int minutes) {
    // Special treatment for hours
    if (minutes > 59 && minutes % 60 == 0) {
      // Convert minutes to hours
      minutes = Period.minutes(minutes).toStandardHours().hours
      return ceilDateHours(date, minutes)
    }
    // ----------
    int unroundedMinutes = date.minuteOfHour
    int mod = unroundedMinutes % minutes
    DateTime newDate = date.plusMinutes((mod == 0) ? minutes : minutes - mod)
    // Clear seconds and return new date
    return newDate.withSecondOfMinute(0)
  }

  // Round down to lower hours
  static DateTime floorDateHours(DateTime date, int hours) {
    int unroundedHours = date.hourOfDay
    int mod = unroundedHours % hours
    DateTime newDate = date.minusHours(mod)
    // Clear minutes and seconds and return new date
    newDate = newDate.withMinuteOfHour(0)
    return newDate.withSecondOfMinute(0)
  }

  // Round up to upper hours
  static DateTime ceilDateHours(DateTime date, int hours) {
    int unroundedHours = date.hourOfDay
    int mod = unroundedHours % hours
    DateTime newDate = date.plusHours((mod == 0) ? hours : (hours - mod))
    // Clear minutes and seconds and return new date
    newDate = newDate.withMinuteOfHour(0)
    return newDate.withSecondOfMinute(0)
  }

  // Time zone fix
  static DateTime addUTCOffset(DateTime dateTime) {
    // We need to account for the Europe/Berlin timezone offset while keeping everything at UTC
    // Therefore, we add the time zone difference (+1 or +2 depending on DST) to the UTC date to simulate Europe/Berlin
    // So "now" is really UTC+1 or UTC+2 instead of UTC
    // Important: Any future data analysis should account for the time zone offset
    //def hostTimezone = DateTimeZone.forTimeZone(TimeZone.getDefault()) // get host time zone, should be Europe/Berlin
    def hostTimezone = DateTimeZone.forID("Europe/Berlin"); // Make sure (in comparison to above) that we are using Europe/Berlin
    def offsetMillis = hostTimezone.getOffsetFromLocal(dateTime.getMillis()) // local is UTC by default as set in BootStrap
    dateTime = dateTime.plusMillis(offsetMillis)
    return dateTime
  }

  // For undoing the time zone fix
  static DateTime subtractUTCOffset(DateTime dateTime) {
    def hostTimezone = DateTimeZone.forID("Europe/Berlin"); // Make sure (in comparison to above) that we are using Europe/Berlin
    def offsetMillis = hostTimezone.getOffsetFromLocal(dateTime.getMillis()) // local is UTC by default as set in BootStrap
    dateTime = dateTime.minusMillis(offsetMillis)
    return dateTime
  }

  // Format
  static String formatDateTime(DateTime startDateTime, DateTime endDateTime = null, ConsumptionType type = null, boolean utcFix = true) {
    DateTime displayStartDate = (utcFix ? DateUtils.subtractUTCOffset(startDateTime) : startDateTime)
    def startPattern = ""

    switch (type) {
      case ConsumptionType.MIN5:
      case ConsumptionType.MIN15:
        if (startDateTime.dayOfWeek in (DateTimeConstants.MONDAY..DateTimeConstants.FRIDAY)) {
          startPattern += "'Durchschnittlicher Wochentag' "
        } else {
          startPattern += "'Durchschnittliches Wochenende' "
        }
        break
      case ConsumptionType.MIN30:
      case ConsumptionType.H3:
        startPattern += "'Durchschnittlicher ' EEEE "
        break
      default:
        startPattern += "dd.MM.YYYY "
    }

    startPattern += "HH:mm:ss"
    DateTimeFormatter startFormatter = DateTimeFormat.forPattern(startPattern).withLocale(Locale.GERMAN)
    def formattedStartDate = startFormatter.print(displayStartDate)

    def formattedDate = formattedStartDate

    if (endDateTime) {
      DateTime displayEndDate = (utcFix ? DateUtils.subtractUTCOffset(endDateTime) : endDateTime).minusSeconds(1)
      DateTimeFormatter endFormatter = DateTimeFormat.forPattern("HH:mm:ss").withLocale(Locale.GERMAN)
      def formattedEndDate = endFormatter.print(displayEndDate)
      formattedDate += " - " + formattedEndDate
    }

    formattedDate
  }
}
