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

import java.math.RoundingMode

class HomeController {

  def springSecurityService
  def rankingService

  def index() {
    if (springSecurityService.isLoggedIn()) {
      redirect(action: "live")
    } else {
      redirect(action: "welcome")
    }
  }

  // Displayed to users that are not logged in
  def welcome() {
    [nav: "welcome"]
  }

  // Displayed to logged in users
  def live() {
    [nav: "live"]
  }

  // History
  def history() {
    [nav: "history"]
  }

  // Ranking
  def ranking() {

    // Find current household and peergroup
    def household = Household.findById(springSecurityService.currentUser?.id)
    def peergroup = household?.peergroup

    def ranking = peergroup.households.collect { h ->
      def currentSum = rankingService.determineRankingValue(h.id)
      def rankingValue = currentSum / (h.referenceRankingValue ?: 1.0)

      // Display max 200%
      def displayValue = rankingValue.min(2.0).max(0.0)
      displayValue *= 100.0
      displayValue /= 2.0
      displayValue = displayValue.setScale(2, RoundingMode.HALF_UP)

      // Ranking value unlimited (tooltip)
      rankingValue = rankingValue.max(0.0)
      rankingValue *= 100
      rankingValue = rankingValue.setScale(2, RoundingMode.HALF_UP)

      // Determine display class
      def displayClass
      if (displayValue >= 200.0) {
        displayClass = "progress-danger"
      } else if (displayValue >= 150.0) {
        displayClass = "progress-warning"
      } else {
        displayClass = "progress-success"
      }

      [name: h.fullName, rankingValue: rankingValue, displayValue: displayValue, displayClass: displayClass,
          display: (h.referenceRankingValue > 0)]
    }

    ranking = ranking.sort { a, b ->
      (a.display ? a.rankingValue : Double.MAX_VALUE) <=> (b.display ? b.rankingValue : Double.MAX_VALUE) ?:
        a.name <=> b.name
    }

    // Badge classes
    def badgeClasses = ["badge-success", "badge-warning", "badge-info", "badge-error", "badge-inverse"]
    ranking.eachWithIndex { map, i ->
      map.badge = (i < badgeClasses.size()) ? badgeClasses.get(i) : ""
    }

    [nav: "ranking", ranking: ranking]
  }
}
