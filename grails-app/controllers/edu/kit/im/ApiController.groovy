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

import grails.converters.JSON

class ApiController {

  def apiService

  def consumption() {
    def jsonStatus

    try {
      // Extract json
      def payload = JSON.parse(params.json)
      apiService.processConsumption(request, payload)

      response.status = 200 // OK
      jsonStatus = [status: [code: 200]] as JSON

    } catch (ApiException e) {
      def apiError = new ApiError()
      apiError.description = e.message
      apiError.clientIp = apiService.getClientIP(request)
      apiError.json = params.json
      apiError.save()

      response.status = e.code // Bad Request
      jsonStatus = [status: [code: e.code, message: e.message]] as JSON

    } catch (Exception e) {
      def apiError = new ApiError()
      apiError.description = e.message
      apiError.clientIp = apiService.getClientIP(request)
      apiError.json = params.json
      apiError.save()

      response.status = 400 // Bad Request
      jsonStatus = [status: [code: 400, message: "Invalid JSON"]] as JSON
    }

    render jsonStatus
  }

  def fail() {
    response.status = 502
    render "502 Bad Gateway (API FAIL)"
  }
}
