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

package edu.kit.im.messages

import edu.kit.im.enums.EventType
import org.apache.commons.lang.builder.ToStringBuilder
import org.joda.time.DateTime
import org.joda.time.Duration

class EventMessage implements Serializable {
  EventType type
  DateTime date
  Duration duration
  String url
  String parameters
  Long householdId

  EventMessage(EventType type, DateTime date, Duration duration, String url, String parameters, Long householdId) {
    this.type = type
    this.date = date
    this.duration = duration
    this.url = url
    this.parameters = parameters
    this.householdId = householdId
  }

  EventMessage(EventType type, Long householdId) {
    this.type = type
    this.householdId = householdId
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).
        append("type", type).
        append("date", date).
        append("duration", duration).
        append("url", url).
        append("parameters", parameters).
        append("householdId", householdId).
        toString();
  }
}
