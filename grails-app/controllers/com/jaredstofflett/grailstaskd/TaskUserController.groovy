package com.jaredstofflett.grailstaskd
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class TaskUserController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond TaskUser.list(params), model: [taskUserCount: TaskUser.count()]
    }

    def show(TaskUser taskUser) {
        respond taskUser
    }

    def create() {
        respond new TaskUser(params)
    }

    @Transactional
    def save(TaskUser taskUser) {
        if (taskUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (taskUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond taskUser.errors, view: 'create'
            return
        }

        taskUser.save flush: true
        def userRole=Role.findByAuthority('ROLE_USER')
        UserRole.create taskUser, userRole
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'taskUser.label', default: 'TaskUser'), taskUser.id])
                redirect taskUser
            }
			'*' { respond taskUser, [status: CREATED] }
        }
    }

    def edit(TaskUser taskUser) {
        respond taskUser
    }

    @Transactional
    def update(TaskUser taskUser) {
        if (taskUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (taskUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond taskUser.errors, view: 'edit'
            return
        }

        taskUser.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'taskUser.label', default: 'TaskUser'), taskUser.id])
                redirect taskUser
            }
			'*' { respond taskUser, [status: OK] }
        }
    }

    @Transactional
    def delete(TaskUser taskUser) {

        if (taskUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        taskUser.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'taskUser.label', default: 'TaskUser'), taskUser.id])
                redirect action: "index", method: "GET"
            }
			'*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'taskUser.label', default: 'TaskUser'), params.id])
                redirect action: "index", method: "GET"
            }
			'*' { render status: NOT_FOUND }
        }
    }
}

