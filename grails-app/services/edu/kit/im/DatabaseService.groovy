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
import grails.converters.JSON
import grails.validation.ValidationException
import edu.kit.im.messages.*

class DatabaseService {

  static rabbitQueue = "db"

  def apiService

  void handleMessage(ConsumptionMessage message) {
    try {
      def jsonObject = JSON.parse(message.json)
      apiService.processConsumption(message, jsonObject)
    } catch (Exception e) {
      rabbitSend "wenergy", "db", new ApiErrorMessage(e.message, message.clientIp, null, message.json)
    }
  }

  void handleMessage(ApiErrorMessage message) {
    def apiError = new ApiError()
    apiError.description = message.description
    apiError.clientIp = message.clientIp
    apiError.householdId = message.householdId
    apiError.json = message.json
    try {
      apiError.save(failOnError: true)
    } catch (ValidationException e) {
      log.error apiError.errors
    }
  }

  void handleMessage(ReferenceConsumptionMessage message) {
    Household household = Household.get(message.householdId)
    household.referenceConsumptionValue = message.referenceValue
    try {
      household.save(failOnError: true)
    } catch (ValidationException e) {
      log.error household.errors
    }
  }

  void handleMessage(ReferenceRankingMessage message) {
    Household household = Household.get(message.householdId)
    household.referenceRankingValue = message.referenceValue
    try {
      household.save(failOnError: true)
    } catch (ValidationException e) {
      log.error household.errors
    }
  }

  void handleMessage(EventMessage message) {
    def household = Household.get(message.householdId)
    def event

    switch (message.type) {
      case EventType.LOGIN:
      case EventType.LOGOUT:
        event = new Event(type: message.type, household: household)
        break
      default:
        event = new Event(type: message.type, url: message.url, parameters: message.parameters,
            duration: message.duration, household: household)
    }
    try {
      event?.save(failOnError: true)
    } catch (ValidationException e) {
      log.error event?.errors
    }
  }
}
