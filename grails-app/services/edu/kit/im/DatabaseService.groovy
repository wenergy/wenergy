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
import edu.kit.im.messages.ReferenceConsumptionMessage
import grails.converters.JSON
import grails.validation.ValidationException
import edu.kit.im.messages.ReferenceRankingMessage

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
}
