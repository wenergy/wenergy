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

  def dataService

  def index() {
    def jsonStatus = [status: [code: 400, message: "Invalid request"]] as JSON
    response.status = 400
    render jsonStatus
  }

  def daily() {
    try {
      // Parse params
      long jsDate = params.date as long
      def date = new DateTime(jsDate)
      def dailyData = dataService.getDailyData(date)

      log.error dailyData

      def json = [status: [code: 200, message: "blaha"]] as JSON
      response.status = 200
      render json

    } catch (Exception e) {
      def json = [status: [code: 500, message: "Could not load daily data"]] as JSON
      response.status = 500
      render json
    }
  }

  def weekly() {

  }

  def monthly() {

  }
}
