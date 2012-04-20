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
