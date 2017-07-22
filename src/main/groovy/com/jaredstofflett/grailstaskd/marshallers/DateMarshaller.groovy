package com.jaredstofflett.grailstaskd.marshallers

import grails.converters.JSON

// Custom marshaller to get dates in the correct format for task json
class DateMarshaller {
    void register() {
        JSON.registerObjectMarshaller(Date) {
            return it?.format("yyyyMMdd'T'HHmmss'Z'")
        }
    }
}
