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
import org.joda.time.DateTime
import org.joda.time.Duration
import edu.kit.im.enums.EventType
import edu.kit.im.utils.DateUtils
import edu.kit.im.messages.ApiErrorMessage
import edu.kit.im.messages.EventMessage

class DataController {

  static allowedMethods = [index: "POST", welcome: "POST", consumption: "POST", live: "POST", event: "POST"]

  def dataService

  def index() {
    def jsonStatus = [status: [code: 400, message: "Invalid request"]] as JSON
    response.status = 400
    render jsonStatus
  }

  def welcome() {
    try {
      // Dispatch
      def data = dataService.getWelcomeData()
      def json = [
          status: [code: 200],
          data: data
      ] as JSON

      response.status = 200
      render json

    } catch (Exception e) {
      def json = [
          status:
              [
                  code: 500,
                  message: e.toString().encodeAsHTML()
                  //stack: ApiUtils.getStackTraceAsString(e).encodeAsHTML()
              ]
      ] as JSON

      response.status = 500
      render json
    }
  }

  def consumption() {
    try {
      // Parse params
      long jsDate = params.date as long
      def date = new DateTime(jsDate)

      def deltaTime = null
      if (params.deltaTime) {
        long jsDeltaDate = params.deltaTime as long
        if (jsDeltaDate > 0) {
          deltaTime = new DateTime(jsDeltaDate)
        }
      }

      def precision = params.precision as Integer

      // Get consumption data
      def data = dataService.getConsumptionData(date, params.interval, precision, params.dataType, deltaTime)
      def json = [
          status: [code: 200],
          data: data,
      ] as JSON

      response.status = 200
      render json

    } catch (Exception e) {
      def json = [
          status:
              [
                  code: 500,
                  message: e.toString().encodeAsHTML()
                  //stack: ApiUtils.getStackTraceAsString(e).encodeAsHTML()
              ]
      ] as JSON

      response.status = 500
      render json
    }
  }

  def live() {
    try {
      // Parse params
      def deltaTime = null
      if (params.deltaTime) {
        long jsDate = params.deltaTime as long
        if (jsDate > 0) {
          deltaTime = new DateTime(jsDate)
        }
      }

      int numberOfValues = params.numberOfValues as int

      // Dispatch
      def data = dataService.getLiveData(numberOfValues, deltaTime)
      def json = [
          status: [code: 200],
          data: data
      ] as JSON

      response.status = 200
      render json
    } catch (Exception e) {
      def json = [
          status:
              [
                  code: 500,
                  message: e.toString().encodeAsHTML()
                  //stack: ApiUtils.getStackTraceAsString(e).encodeAsHTML()
              ]
      ] as JSON

      response.status = 500
      render json
    }
  }

  def event() {
    try {
      def householdId = params.uid as Long
      def url = params.loc
      def parameters = params.par
      def duration = params.dur as Long

      rabbitSend "wenergy", "db", new EventMessage(EventType.PAGE_VIEW, new DateTime(),
          new Duration(duration), url, parameters, householdId)

      response.status = 200 // OK
      def json = [status: [code: 200]] as JSON
      render json
    } catch (Exception e) {
      def json = [
          status:
              [
                  code: 500,
                  message: e.toString().encodeAsHTML()
                  //stack: ApiUtils.getStackTraceAsString(e).encodeAsHTML()
              ]
      ] as JSON

      response.status = 500
      render json
    }
  }
}