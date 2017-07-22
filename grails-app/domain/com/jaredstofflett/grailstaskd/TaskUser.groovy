package com.jaredstofflett.grailstaskd

class TaskUser extends User {
    String serverAddress
    int serverPort
    byte[] caCert
    byte[] userCert
    byte[] userKey
    String syncKey
    Date syncDate = null
    String orgName
    String taskUserKey
    String taskUserName

    static constraints = {
        caCert maxSize: 1024 * 1024
        userCert maxSize: 1024 * 1024
        userKey maxSize: 1024 * 1024
        syncKey nullable: true
        syncDate nullable: true
    }

    String toString() {
        return username
    }
}

