<g:form action="complete">
    <table >
        <tr><td>complete</td><td>description</td><td>project</td><td>due</td><td>status</td></tr>
        <g:each var="task" in="${taskList}">
            <tr>
                <g:if test="${task.status !='deleted' && task.status!='recurring'}">
                    <td><g:checkBox name="completeTasks" value="${task.uuid}" checked="${false}"/>
                    </g:if>
                    <g:else>
                    <td></td>
                </g:else>
                <td>${task.description}</td><td>${task.project}</td>
                <td>${task.due}</td>
                <td>${task.status}</td>
            </tr>
        </g:each>
    </table>
    <g:submitButton name="complete" value="complete" />
</g:form>