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

import grails.validation.ValidationException

class UserController {

  def springSecurityService

  def index() {
    [household: springSecurityService.currentUser]
  }

  def password() {
    def map = [:]
    if (params.password && request.method == "POST") {
      if (params.password != params.password2) {
        map.status = "error"
        map.statusText = "Passwörter stimmen nicht überein"
      } else {
        def household = springSecurityService.currentUser
        household.password = params.password
        try {
          household.save(flush: true, failOnError: true)
          springSecurityService.reauthenticate(household.username)
          map.status = "success"
          map.statusText = "Passwort erfolgreich geändert"
        } catch (ValidationException exception) {
          map.status = "error"
          map.statusText = "Passwort muss mindestens 3 Zeichen lang sein"
        }
      }
    }
    map
  }

  def themes() {
    def household = springSecurityService.currentUser
    [themes: ThemeType.values(), currentTheme: household.theme]
  }

  def changeTheme() {
    if (params.id) {
      def household = springSecurityService.currentUser
      try {
        household.theme = ThemeType.valueOf(params.id)
        household.save()
      } catch (Exception e) {
        log.error e
      }
    }
    redirect(action: "themes")
  }
}
