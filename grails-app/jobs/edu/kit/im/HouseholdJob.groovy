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

class HouseholdJob {

  static triggers = {
    // "0 0 0 * * ?" // every day at midnight
    // "0 0/15 6-22 * * ?" // every 15 minutes between 6am-11pm
    // '0 * * * * ?' // every minute (test)
    cron cronExpression: "0 0 3 * * ?" // wEnergy Scheduler, every day at 3am
  }

  def householdService

  def execute() {
    householdService.run()
  }

}
