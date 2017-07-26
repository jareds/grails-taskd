package com.jaredstofflett.grailstaskd

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
@Secured('ROLE_USER')
class TaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def springSecurityService

    def index() {
        def user = springSecurityService.currentUser
        SyncUtils.addUpdatedTasks(user)
        def tasks = Task.findAllByUserAndStatus(user, "pending")
        respond tasks, model: [taskCount: tasks.size()]
    }
    def over() {
        def user = springSecurityService.currentUser
        SyncUtils.addUpdatedTasks(user)
        def tasks = Task.findAllByUserAndStatusAndDueLessThanEquals(user, "pending",new Date())
        respond tasks, model: [taskCount: tasks.size()]
    }
    def all() {
        def user = springSecurityService.currentUser
        SyncUtils.addUpdatedTasks(user)
        def tasks = Task.findAllByUser(user)
        respond tasks, model: [taskCount: tasks.size()]
    }
    def show(Task task) {
        respond task
    }

    def create() {
        respond new Task(params)
    }

    @Transactional
    def save(Task task) {
        if (task == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }
        def curDate=new Date()
        task.user=springSecurityService.currentUser
        task.entry=curDate
        task.modified=curDate
        task.uuid=UUID.randomUUID().toString()
        task.status="pending"
        task.validate() //Required since we manually set some propertys that hasErrors doesn't know about with out this call
        if (task.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond task.errors, view: 'create'
            return
        }

        task.save flush: true, failOnError:true
        SyncUtils.syncTasks(springSecurityService.currentUser)
        redirect uri:"/"
    }

    def edit(Task task) {
        respond task
    }

    @Transactional
    def update(Task task) {
        if (task == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (task.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond task.errors, view: 'edit'
            return
        }

        task.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'task.label', default: 'Task'), task.id])
                redirect task
            }
			'*' { respond task, [status: OK] }
        }
    }

    @Transactional
    def delete(Task task) {

        if (task == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        task.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'task.label', default: 'Task'), task.id])
                redirect action: "index", method: "GET"
            }
			'*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'task.label', default: 'Task'), params.id])
                redirect action: "index", method: "GET"
            }
			'*' { render status: NOT_FOUND }
        }
    }

    def complete() {
        params.list("completeTasks").each {
            SyncUtils.completeTask(Task.findByUserAndUuid(springSecurityService.currentUser, it))
        }
        SyncUtils.syncTasks(springSecurityService.currentUser)
        SyncUtils.addUpdatedTasks(springSecurityService.currentUser)
        render('hello world')
    }
}

