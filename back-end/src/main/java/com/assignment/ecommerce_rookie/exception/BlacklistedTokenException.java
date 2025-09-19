package com.assignment.ecommerce_rookie.exception;

public class BlacklistedTokenException extends RuntimeException{
    public BlacklistedTokenException(String message) {
        super(message);
    }
}
