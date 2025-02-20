package com.billing.invoice.exception_handler.exceptions.not_found;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
