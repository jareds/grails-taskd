import com.jaredstofflett.grailstaskd.*
import com.jaredstofflett.grailstaskd.marshallers.DateMarshaller
import com.jaredstofflett.grailstaskd.marshallers.TaskMarshaller
import grails.util.Environment
import grails.core.GrailsApplication
class BootStrap {
    GrailsApplication grailsApplication
    def init = { servletContext ->
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")) //nTaskd uses UTC so we need to for date comparrisons to work
        def dm = new DateMarshaller()
        dm.register()
        def tm = new TaskMarshaller()
        tm.register()
        //Insert admin and user roles if they do not exist.
        if (Role.findByAuthority('ROLE_ADMIN') ==null ) {
            new Role(authority:'ROLE_ADMIN').save()
        }
        if (Role.findByAuthority('ROLE_USER') == null) {
            new Role(authority: 'ROLE_USER').save()
        }
        //Insert admin and user account if defined in our config
        if (grailsApplication.config.getProperty('accounts.admin.name') != null && AdminUser.findByUsername(grailsApplication.config.getProperty('accounts.admin.name'))== null) {
            def adminUser = new AdminUser(username: grailsApplication.config.getProperty('accounts.admin.name'), password: grailsApplication.config.getProperty('accounts.admin.password')).save()
            def adminRole=Role.findByAuthority('ROLE_ADMIN')
            UserRole.create adminUser, adminRole
            UserRole.withSession {
                it.flush()
                it.clear()
            }
        }
        if (grailsApplication.config.getProperty('accounts.user.name') != null && TaskUser.findByUsername(grailsApplication.config.getProperty('accounts.user.name'))== null) {
            def userRole=Role.findByAuthority('ROLE_USER')
            def taskUser = new TaskUser(username: grailsApplication.config.getProperty('accounts.user.name'), password: grailsApplication.config.getProperty('accounts.user.password'),
                serverAddress: grailsApplication.config.getProperty('accounts.user.serverAddress'),
                serverPort: grailsApplication.config.getProperty('accounts.user.serverPort'),
                orgName: grailsApplication.config.getProperty('accounts.user.org'),
                taskUserName: grailsApplication.config.getProperty('accounts.user.taskUser'),
                taskUserKey: grailsApplication.config.getProperty('accounts.user.taskUserKey'),
                caCert: new File(grailsApplication.config.getProperty('accounts.user.caCert')).getBytes(),
                userCert: new File(grailsApplication.config.getProperty('accounts.user.userCert')).getBytes(),
                userKey: new File(grailsApplication.config.getProperty('accounts.user.userKey')).getBytes(),
                syncKey: null).save()
            UserRole.create taskUser, userRole
            UserRole.withSession {
                it.flush()
                it.clear()
            }
        }
    }
    def destroy = {
    }
}

