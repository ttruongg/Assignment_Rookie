package com.assignment.ecommerce_rookie.exception;

public class NotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;


    public NotFoundException(String resourceName, String field, String fieldName) {
        super(String.format(resourceName + " not found with " + field + ":" + fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;

    }

    public NotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format(resourceName + " not found with " + field + ":" + fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
