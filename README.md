# grails-taskd
This repository is no longer under active development since I've found [Remember the Milk](https://www.rememberthemilk.com) with it's iOS app and [rtm-cli](https://github.com/dwaring87/rtm-cli) to be a better fit for me then Taskwarrior.
This repository was mainly a learning exercise to familiarize my self with Grails 3. It is a web based front end to [taskd](https://tasktools.org/projects/taskd.html)


## Limitations

Currently this project allows you to view all tasks, all tasks that are not completed, and all overdue tasks. You can add a task but can not set a due date, tags, or recurrence. Additional features such as viewing lists of projects, creating tasks, etc may be supported depending on time and interest.
Currently this application does not support some major features of the taskwarrior JSON format such as priority, annotations, etc. If a task is found with these features an error is logged and the task is not added to the database.
Use this application at your own risk. In my usage I have not found any data loss bugs, but I also have not extensively tested. If you are going to use this against valuable data I would suggest frequent export of your task data using taskwarrior so if something catastrophic goes wrong you can import all your tasks from a backup and sync from scratch with taskd.

## Running

A default administrator and user are configured in application.yml for development. The default user is configured to work with a taskd server running on localhost. The default user assumes that the taskd server is built from the Docker file in the docker subdirectory. This Docker image is configured with a taskd user and all the required information such as user cert and user key. Read the application.yml file to configure taskwarrior to talk to the taskd server.
