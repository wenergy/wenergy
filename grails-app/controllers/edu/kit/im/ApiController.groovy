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

import grails.converters.JSON

class ApiController {

  def allowedMethods = [consumption: "POST", debug: "GET"]

  def apiService

  def debug() {
    def json = [id: "de:ad:be:ef:fe:ed", t: 1317813516000, p: 123, q: 456] as JSON

    try {
      def convertedJson = JSON.parse(json.toString())
      apiService.processConsumption(convertedJson)
    } catch (Exception e) {
      log.error e
    }

    render json
  }

  def consumption() {
    def jsonStatus

    try {
      // Extract json
      def payload = request.JSON
      apiService.processConsumption(payload)

      response.status = 200 // OK
      jsonStatus = [status: [code: 200]] as JSON

    } catch (ApiException e) {

      log.error e
      response.status = e.code // Bad Request
      jsonStatus = [status: [code: e.code, message: e.message]] as JSON

    } catch (Exception e) {
      log.error e.printStackTrace()
      log.error e
      response.status = 400 // Bad Request
      jsonStatus = [status: [code: 400, message: "Invalid JSON"]] as JSON

    }

    render jsonStatus
  }
}
