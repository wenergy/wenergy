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

import edu.kit.im.enums.EventType
import org.apache.commons.lang.builder.ToStringBuilder
import org.joda.time.DateTime
import org.joda.time.Duration

class Event implements Serializable {

  EventType type
  DateTime date = new DateTime()

  // Viewer information
  Duration duration
  String url
  String parameters

  // Grails information
  DateTime dateCreated

  // Relationships
  static belongsTo = [household: Household]

  static constraints = {
    type(nullable: false)
    date(nullable: false)
    duration(nullable: true)
    url(nullable: true)
    parameters(nullable: true)
    dateCreated()
  }

  static mapping = {
    cache(true)
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).
        append("id", id).
        append("type", type).
        append("date", date).
        append("duration", duration).
        append("url", url).
        append("parameters", parameters).
        append("dateCreated", dateCreated).
        append("version", version).
        toString();
  }
}
