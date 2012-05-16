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

import edu.kit.im.messages.ApiErrorMessage
import edu.kit.im.messages.ConsumptionMessage
import grails.converters.JSON
import org.joda.time.DateTime

class ApiController {

  def apiService

  def consumption() {
    String jsonParam = params.json
    def jsonObject = JSON.parse(jsonParam)
    try {
      apiService.validateConsumption(jsonObject)
      rabbitSend "wenergy", "db", new ConsumptionMessage(apiService.getClientIP(request), jsonParam, new DateTime())
    } catch (Exception e) {
      rabbitSend "wenergy", "db", new ApiErrorMessage(e.message, apiService.getClientIP(request), null, jsonParam)
    }
    def jsonStatus = [status: [code: 200]] as JSON
    response.status = 200 // OK
    render jsonStatus
  }

  def fail() {
    response.status = 502
    render "502 Bad Gateway (API FAIL)"
  }
}
