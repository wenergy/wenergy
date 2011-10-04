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

class RegistrationController {

  def springSecurityService

  def index() {
    if (springSecurityService.isLoggedIn()) {
      flash.message = "You are already registered."
      redirect(controller: "home", action: "dashboard")
      return
    }
    [nav: "registration"]
  }

  def register() {
    if (springSecurityService.isLoggedIn()) {
      flash.message = "You are already registered."
      redirect(controller: "home", action: "dashboard")
      return
    }

    if (params.submit) {
      def householdInstance = new Household(params)
      if (!householdInstance.save(flush: true)) {
        return [nav: "registration", householdInstance: householdInstance]
      }

//      TODO: Include Mail Plugin
//      http://www.grails.org/plugin/mail

      render(view: "confirm", model: [nav: "registration", householdInstance: householdInstance])
      return
    }

    [nav: "registration"]
  }

  def activate() {
    if (springSecurityService.isLoggedIn()) {
      flash.message = "You are already registered."
      redirect(controller: "home", action: "dashboard")
      return
    }
    [nav: "registration"]
  }
}
