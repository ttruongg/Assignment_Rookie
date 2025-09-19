package com.assignment.ecommerce_rookie.exception;

public class TokenMissingException extends RuntimeException{
    public TokenMissingException(String message) {
        super(message);
    }
}
