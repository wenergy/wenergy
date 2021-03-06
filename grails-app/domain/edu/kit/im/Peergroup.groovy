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

class Peergroup implements Serializable {

  String name
  DateTime dateCreated

  static hasMany = [households: Household]

  static constraints = {
    name(blank: false, unique: true)
    households(bindable: true)
    dateCreated()
  }

  static mapping = {
    cache(true)
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).
        append("id", id).
        append("name", name).
        append("dateCreated", dateCreated).
        append("version", version).
        toString();
  }
}
