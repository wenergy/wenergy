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
import org.joda.time.DateTime

class DataController {

  def allowedMethods = [index: "POST", data: "POST"]

  def dataService

  def index() {
    def jsonStatus = [status: [code: 400, message: "Invalid request"]] as JSON
    response.status = 400
    render jsonStatus
  }

  def data() {
    try {
      // Parse params
      long jsDate = params.date as long
      def date = new DateTime(jsDate)

      // Get consumption data
      def data
      // Time interval
      def low
      def high

      switch (params.interval) {
        case "weekly":
          data = dataService.getWeeklyData(date, true)
          low = date.withTimeAtStartOfDay().dayOfWeek().withMinimumValue() // Mon, 00:00:00
          high = low.plusWeeks(1).minusMinutes(30) // Sun, 23:30:00 // last start time for 30min interval
          break;
        case "monthly":
          data = dataService.getMonthlyData(date, true)
          low = date.withTimeAtStartOfDay().dayOfMonth().withMinimumValue() // 1st of month, 00:00:00
          high = low.plusMonths(1).minusHours(3) // 23:55:00 // last start time for 3h interval
          break;
        case "daily":
        default:
          data = dataService.getDailyData(date, true)
          low = date.withTimeAtStartOfDay() // 00:00:00
          high = low.plusDays(1).minusMinutes(5) // 23:55:00 // last start time for 5min interval
          break;
      }

      def json = [
          status: [code: 200],
          data: data,
          time: [low: low?.getMillis(), high: high?.getMillis()]
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
}
