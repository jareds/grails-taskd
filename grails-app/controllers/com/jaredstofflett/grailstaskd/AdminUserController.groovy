package com.jaredstofflett.grailstaskd

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class AdminUserController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond AdminUser.list(params), model: [adminUserCount: AdminUser.count()]
    }

    def show(AdminUser adminUser) {
        respond adminUser
    }

    def create() {
        respond new AdminUser(params)
    }

    @Transactional
    def save(AdminUser adminUser) {
        if (adminUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (adminUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond adminUser.errors, view: 'create'
            return
        }

        adminUser.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'adminUser.label', default: 'AdminUser'), adminUser.id])
                redirect adminUser
            }
			'*' { respond adminUser, [status: CREATED] }
        }
    }

    def edit(AdminUser adminUser) {
        respond adminUser
    }

    @Transactional
    def update(AdminUser adminUser) {
        if (adminUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (adminUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond adminUser.errors, view: 'edit'
            return
        }

        adminUser.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'adminUser.label', default: 'AdminUser'), adminUser.id])
                redirect adminUser
            }
			'*' { respond adminUser, [status: OK] }
        }
    }

    @Transactional
    def delete(AdminUser adminUser) {

        if (adminUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        adminUser.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'adminUser.label', default: 'AdminUser'), adminUser.id])
                redirect action: "index", method: "GET"
            }
			'*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminUser.label', default: 'AdminUser'), params.id])
                redirect action: "index", method: "GET"
            }
			'*' { render status: NOT_FOUND }
        }
    }
}

