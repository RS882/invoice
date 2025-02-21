package com.billing.invoice.exception_handler.exceptions.bad_request;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
