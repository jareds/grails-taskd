package com.jaredstofflett.grailstaskd

import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_USER')
class UserTestController {

    def index() {
        render("hello world")
    }
}

