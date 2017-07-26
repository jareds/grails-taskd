package com.jaredstofflett.grailstaskd

class Task implements Serializable {
    TaskUser user
    Date entry
    Date modified
    String uuid
    String description
    String project
    String status
    Date end
    Date due
    String imask
    String mask
    String parent
    String recur
    static hasMany = [tags: String]
    static constraints = {
        project nullable: true
        end nullable: true
        parent nullable: true
        imask nullable: true
        due nullable: true
        recur nullable: true
        mask nullable: true
        // Following fields are uuid
        uuid maxSize:36
        parent maxSize:36
        // Following fields can get huge
        mask maxSize:1024*1024*3
        description maxSize:1024*1024*3
        imask maxSize:1024*1024*3
    }
    static mapping = {
        id composite: ['uuid', 'user']
    }
}

