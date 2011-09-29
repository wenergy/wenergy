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
    instance ? instance.delete(flush: flush) : false
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
