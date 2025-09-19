package com.assignment.ecommerce_rookie.exception;

public class InvalidToken extends RuntimeException{
    public InvalidToken(String message) {
        super(message);
    }
}
