package com.example.commercehub.shared.exception;

public class InvalidProductUpdateException extends RuntimeException {
    public InvalidProductUpdateException(String message) {
        super(message);
    }
}