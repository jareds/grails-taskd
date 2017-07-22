package com.jaredstofflett.grailstaskd

import org.grails.web.json.JSONElement

class SyncData {
    ArrayList<JSONElement> tasks
    int code
    String status
    String syncKey
}
