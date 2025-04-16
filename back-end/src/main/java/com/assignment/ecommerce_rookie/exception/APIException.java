package com.assignment.ecommerce_rookie.exception;

public class APIException extends RuntimeException{

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
