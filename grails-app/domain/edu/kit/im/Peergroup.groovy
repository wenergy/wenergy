package edu.kit.im

class Peergroup {

    String groupName

    static hasMany = [households : Household]
    static belongsTo = Household

    static constraints = {
        groupName(nullable: false)
    }
}
