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

import org.apache.commons.lang.builder.ToStringBuilder

class ApiErrorMessage implements Serializable {
  String description
  String clientIp
  String householdId
  String json

  ApiErrorMessage(String description, String clientIp, String householdId, String json) {
    this.description = description
    this.clientIp = clientIp
    this.householdId = householdId
    this.json = json
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).
        append("description", description).
        append("clientIp", clientIp).
        append("householdId", householdId).
        append("json", json).
        toString();
  }
}
