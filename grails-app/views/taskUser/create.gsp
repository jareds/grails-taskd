<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'taskUser.label', default: 'TaskUser')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-taskUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                </ul>
            </div>
            <div id="create-taskUser" class="content scaffold-create" role="main">
                <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.taskUser}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${this.taskUser}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                        </g:eachError>
                </ul>
            </g:hasErrors>
            <g:uploadForm action="save">
                <fieldset class="form">
                    <f:field bean="taskUser" property="username"/>
                    <f:field bean="taskUser" property="password"/>
                    <f:field bean="taskUser" property="serverAddress"/>
                    <f:field bean="taskUser" property="serverPort"/>
                    <f:field bean="taskUser" property="caCert"/>
                    <f:field bean="taskUser" property="userCert"/>
                    <f:field bean="taskUser" property="userKey"/>
                    <f:field bean="taskUser" property="taskUserName"/>
                    <f:field bean="taskUser" property="taskUserKey"/>
                    <f:field bean="taskUser" property="orgName"/>
                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                </fieldset>
            </g:uploadForm>
        </div>
    </body>
</html>
