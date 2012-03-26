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

import org.apache.commons.lang.builder.HashCodeBuilder

class HouseholdRole implements Serializable {

  Household household
  Role role

  boolean equals(other) {
    if (!(other instanceof HouseholdRole)) {
      return false
    }

    other.household?.id == household?.id &&
        other.role?.id == role?.id
  }

  int hashCode() {
    def builder = new HashCodeBuilder()
    if (household) builder.append(household.id)
    if (role) builder.append(role.id)
    builder.toHashCode()
  }

  static HouseholdRole get(long householdId, long roleId) {
    find 'from HouseholdRole where household.id=:householdId and role.id=:roleId',
        [householdId: householdId, roleId: roleId]
  }

  static HouseholdRole create(Household household, Role role, boolean flush = false) {
    new HouseholdRole(household: household, role: role).save(flush: flush, insert: true)
  }

  static boolean remove(Household household, Role role, boolean flush = false) {
    HouseholdRole instance = HouseholdRole.findByHouseholdAndRole(household, role)
    if (!instance) {
      return false
    }

    instance.delete(flush: flush)
    true
  }

  static void removeAll(Household household) {
    executeUpdate 'DELETE FROM HouseholdRole WHERE household=:household', [household: household]
  }

  static void removeAll(Role role) {
    executeUpdate 'DELETE FROM HouseholdRole WHERE role=:role', [role: role]
  }

  static mapping = {
    id composite: ['role', 'household']
    version false
  }
}
