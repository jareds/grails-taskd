<g:form controller="task" action="complete">
    <table >
<caption>Task list</caption>    
<thead>
        <tr><th scope="col">complete</th><th scope="col">description</th><th scope="col">project</th><th scope="col">due</th><th scope="col">status</th></tr>
        </thead>
        <tbody>
        <g:each var="task" in="${taskList}">
            <tr>
                <g:if test="${task.status !='deleted' && task.status!='recurring'}">
                    <td><g:checkBox name="completeTasks" value="${task.uuid}" checked="${false}"/></td>
                    </g:if>
                    <g:else>
                    <td></td>
                </g:else>
                <td>${task.description}</td><td>${task.project}</td>
                <td>${task.due}</td>
                <td>${task.status}</td>
            </tr>
        </g:each>
</tbody>
    </table>
    <g:submitButton name="complete" value="complete" />
</g:form>