package com.jaredstofflett.grailstaskd
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Secured('ROLE_USER')
class ProjectsController {
def springSecurityService
    def index() {
        def projects = Task.withCriteria {
  projections {
    distinct("project")
  }
  eq ('user',springSecurityService.currentUser)
  eq ('status', "pending")
}
    render (view:'index.gsp', model:[projects:projects])
}
def show () {
    def projectTasks=Task.findAllByProjectAndUserAndStatus(params.id,springSecurityService.currentUser,"pending")
    render (view:"show", model: [taskList:projectTasks])
}
}
