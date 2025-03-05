package com.billing.invoice.exception_handler.exceptions.server_exception.exceptions;

import com.billing.invoice.exception_handler.exceptions.server_exception.ServerIOException;

public class PdfGeneratingException extends ServerIOException {
    public PdfGeneratingException(String message) {
        super("Error generating PDF :"+ message);
    }
}
