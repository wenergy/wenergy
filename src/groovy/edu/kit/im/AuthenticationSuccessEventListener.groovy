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

import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.core.userdetails.User
import grails.validation.ValidationException

class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

  void onApplicationEvent(AuthenticationSuccessEvent e) {
    User user = (User) e?.authentication?.principal

    Household.withTransaction {
      def household = Household.findByUsername(user.username)
      household.addToEvents(new Event(type: EventType.LOGIN))
      try {
        household.save(failOnError: true)
      } catch (ValidationException exception) {
        log.error household.errors
      }
    }
  }
}
