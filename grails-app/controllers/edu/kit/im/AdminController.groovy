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

class AdminController {

  def springSecurityService
  def householdService
  def rankingService

  def index() {
    [groups: Peergroup.getAll().sort { it.name.toLowerCase() }]
  }

  def permissions() {
    [households: Household.getAll().sort { it.fullName.toLowerCase() }]
  }

  def addUserToRole(def userId, def targetRole) {
    def role = Role.findByAuthority(targetRole)
    def user = Household.findById(userId)

    if (user?.authorities?.contains(role)) {
      flash.warning = "${user.fullName} hat bereits die gewünschten Rechte"
    } else {
      HouseholdRole.create(user, role)
    }

    redirect(action: "permissions")
  }

  def removeUserFromRole(def userId, def targetRole) {
    def role = Role.findByAuthority(targetRole)
    def user = Household.findById(userId)
    def currentUser = springSecurityService.currentUser

    if (currentUser == user) {
      flash.error = "Die Rechte für ${user.fullName} können nicht entfernt werden"
    } else {
      if (user?.authorities?.contains(role)) {
        HouseholdRole.remove(user, role)
      }
    }

    redirect(action: "permissions")
  }

  def permissionsAddToUser() {
    addUserToRole(params.id, "ROLE_USER")
  }

  def permissionsRemoveFromUser() {
    removeUserFromRole(params.id, "ROLE_USER")
  }

  def permissionsAddToAdmin() {
    addUserToRole(params.id, "ROLE_ADMIN")
  }

  def permissionsRemoveFromAdmin() {
    removeUserFromRole(params.id, "ROLE_ADMIN")
  }

  def batteries() {

  }

  def status() {

  }

  def controllers() {

  }

  def jobs() {

  }

  def errors() {

  }

  def runHouseholdJob() {
    householdService.run()
  }

  def runRankingJob() {
    rankingService.run()
  }

}
