package com.jaredstofflett.grailstaskd.marshallers

import com.jaredstofflett.grailstaskd.Task

import grails.converters.JSON

// Custom marshaller to get Task objects into proper json format.
class TaskMarshaller {
    void register() {
        JSON.registerObjectMarshaller(Task) { task ->
            def taskMap = [entry      : task.entry,
                modified   : task.modified,
                uuid       : task.uuid,
                description: task.description,
                project    : task.project,
                status     : task.status
            ]
            if (task.end != null) {
                taskMap.put("end", task.end.format("yyyyMMdd'T'HHmmss'Z'"))
            }
            if (task.due != null) {
                taskMap.put("due", task.due.format("yyyyMMdd'T'HHmmss'Z'"))
            }
            if (task.recur != null) {
                taskMap.put("recur", task.recur)
            }
            if (task.imask != null) {
                taskMap.put("imask", task.imask)
            }
            if (task.parent != null) {
                taskMap.put("parent", task.parent)
            }
            if (task.mask != null) {
                taskMap.put("mask", task.mask)
            }
            if (task.tags != null) {
                taskMap.put("tags", task.tags)
            }

            return taskMap
        }
    }
}
