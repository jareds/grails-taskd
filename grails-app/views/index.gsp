<html>
    <head>
        <title>
Main page
        </title>
    </head>
    <body>
<sec:ifNotLoggedIn>
<g:link controller='login' action='auth'>Login</g:link>
</sec:ifNotLoggedIn>
<sec:ifLoggedIn>
<g:link controller="task" action="over">Show overdue tasks</g:link>
<g:link controller="task" action="index">Show all pending tasks</g:link>
<g:link controller="task" action="all">Show all tasks</g:link>
</sec:ifLoggedIn> 
   </body>
</html>