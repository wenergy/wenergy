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

import grails.converters.JSON
import org.codehaus.groovy.runtime.InvokerHelper
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.quartz.CronTrigger
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.impl.matchers.GroupMatcher
import java.math.RoundingMode

class AdminController {

  def springSecurityService
  def rankingService
  Scheduler quartzScheduler

  def index() {
    [groups: Peergroup.getAll().sort { it.name.toLowerCase() }, currentUser: springSecurityService.currentUser]
  }

  def permissions() {
    [households: Household.getAll().sort { it.fullName.toLowerCase() }, currentUser: springSecurityService.currentUser]
  }

  private def addUserToRole(def userId, def targetRole) {
    def role = Role.findByAuthority(targetRole)
    def user = Household.findById(userId)

    if (user?.authorities?.contains(role)) {
      flash.warning = "\"${user.fullName}\" hat bereits die gewünschten Rechte"
    } else {
      HouseholdRole.create(user, role)
    }

    redirect(action: "permissions")
  }

  private def removeUserFromRole(def userId, def targetRole) {
    def role = Role.findByAuthority(targetRole)
    def user = Household.findById(userId)
    def currentUser = springSecurityService.currentUser

    if (currentUser == user) {
      flash.error = "Die Rechte für \"${user.fullName}\" können nicht entfernt werden"
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

  def permissionsEnableUser() {
    def user = Household.findById(params.id)
    def currentUser = springSecurityService.currentUser

    if (user.enabled) {
      flash.warning = "\"${user.fullName}\" ist bereits aktiviert"
    } else {
      user.enabled = true
      user.save()
    }

    redirect(action: "permissions")
  }

  def permissionsDisableUser() {
    def user = Household.findById(params.id)
    def currentUser = springSecurityService.currentUser

    if (currentUser == user) {
      flash.error = "\"${user.fullName}\" kann nicht deaktiviert werden"
    } else {
      user.enabled = false
      user.save()
    }

    redirect(action: "permissions")
  }

  def switchUser() {
    def household = Household.findById(params.id)
    redirect(uri: "/j_spring_security_switch_user?j_username=${household?.username}")
  }

  def statistics() {
    def stats = []

    Household.getAll().each { household ->
      def householdMap = [:]
      householdMap.household = household

      def batteryLevel = Consumption.findByHousehold(household, [max: 1, sort: "date", order: "desc"])?.batteryLevel
      if (batteryLevel) {
        def minBatteryLevel = 2650.0 // V
        def maxBatteryLevel = 3300.0 - minBatteryLevel // V
        batteryLevel -= minBatteryLevel
        batteryLevel = batteryLevel.max(0.0)
        batteryLevel /= maxBatteryLevel
        batteryLevel = batteryLevel.min(1.0).max(0.0)
        batteryLevel = batteryLevel.setScale(2, RoundingMode.HALF_UP)
      }
      householdMap.batteryLevel = batteryLevel

      def lastLogin = Event.findByHouseholdAndType(household, EventType.LOGIN, [max: 1, sort: "date", order: "desc"])
      if (lastLogin) {
        def dateFormatter = DateTimeFormat.mediumDateTime().withLocale(Locale.GERMAN)
        householdMap.lastLogin = dateFormatter.print(lastLogin.date)
      }

      stats << householdMap
    }

    [stats: stats, currentUser: springSecurityService.currentUser]
  }

  def information() {
    def appInfo = ["wEnergy Version": grailsApplication.metadata.'app.version',
        "Grails Version": grailsApplication.metadata.'app.grails.version',
        "Groovy Version": InvokerHelper.getVersion(),
        "JVM Version": System.getProperty("java.version"),
        "Controllers": grailsApplication.controllerClasses?.size(),
        "Domains": grailsApplication.domainClasses?.size(),
        "Services": grailsApplication.serviceClasses?.size(),
        "Tag Libraries": grailsApplication.tagLibClasses?.size(),
        "Jobs": grailsApplication.jobClasses?.size(),
        "Systemzeit": new DateTime(),
        "Zeitzone": new DateTime().getZone()
    ]

    def dataInfo = [:]

    dataInfo["Teilnehmer"] = Household.count()
    dataInfo["Gruppen"] = Peergroup.count()
    dataInfo["Verbrauchswerte"] = Consumption.count()
    dataInfo["Aggregierte Verbrauchswerte"] = AggregatedConsumption.count()
    dataInfo["Events"] = Event.count()
    dataInfo["Api-Fehler"] = ApiError.count()

    def vcapApplicationString = System.getenv("VCAP_APPLICATION")
    def vcapApplication
    if (vcapApplicationString) {
      try {
        vcapApplication = JSON.parse(vcapApplicationString) as JSON
        vcapApplication.prettyPrint = true
      } catch (Exception e) {
        log.error e
      }
    }

    def vcapServicesString = System.getenv("VCAP_SERVICES")
    def vcapServices
    if (vcapServicesString) {
      try {
        vcapServices = JSON.parse(vcapServicesString) as JSON
        vcapServices.prettyPrint = true
      } catch (Exception e) {
        log.error e
      }
    }

    [app: appInfo, data: dataInfo, vcapServices: vcapServices?.encodeAsHTML(), vcapApplication: vcapApplication?.encodeAsHTML()]
  }

  def controllers() {
    // see .gsp
  }

  def jobs() {
    def jobList = []
    def groupNames = quartzScheduler.getJobGroupNames()
    def dateFormatter = DateTimeFormat.mediumDateTime().withLocale(Locale.GERMAN)
    def hostTimezone = DateTimeZone.forID("Europe/Berlin")

    groupNames?.each {jobGroup ->
      def keys = quartzScheduler.getJobKeys(GroupMatcher.groupEquals(jobGroup))

      keys?.each {jobKey ->
        def jobMap = [:]
        def jobTriggers = quartzScheduler.getTriggersOfJob(jobKey)
        jobTriggers.each {trigger ->
          if (trigger instanceof CronTrigger) {
            jobMap.cronExpression = trigger.cronExpression
            jobMap.previousFireTime = trigger.previousFireTime ? dateFormatter.print(new DateTime(trigger.previousFireTime, hostTimezone)) : "-"
            jobMap.nextFireTime = trigger.nextFireTime ? dateFormatter.print(new DateTime(trigger.nextFireTime, hostTimezone)) : "-"
            jobMap.state = quartzScheduler.getTriggerState(trigger.key)
          }
        }

        jobMap.name = jobKey.name
        jobList << jobMap
      }
    }

    [jobs: jobList, now: new DateTime()]
  }

  def jobPause() {
    try {
      quartzScheduler.pauseJob(JobKey.jobKey(params.id))
    } catch (SchedulerException e) {
      flash.error = "Der Job \"${params.id}\" konnte nicht pausiert werden: ${e.message}"
    }
    redirect(action: "jobs")
  }

  def jobResume() {
    try {
      quartzScheduler.resumeJob(JobKey.jobKey(params.id))
    } catch (SchedulerException e) {
      flash.error = "Der Job \"${params.id}\" konnte nicht fortgesetzt werden: ${e.message}"
    }
    redirect(action: "jobs")
  }

  def jobTrigger() {
    try {
      quartzScheduler.triggerJob(JobKey.jobKey(params.id))
      flash.message = "Der Job \"${params.id}\" wurde erfolgreich außerhalb des Zeitplans ausgeführt"
    } catch (SchedulerException e) {
      flash.error = "Der Job \"${params.id}\" konnte nicht ausgeführt werden: ${e.message}"
    }
    redirect(action: "jobs")
  }

  def errors() {
    def errors = ApiError.getAll()
    [errors: errors]
  }
}
