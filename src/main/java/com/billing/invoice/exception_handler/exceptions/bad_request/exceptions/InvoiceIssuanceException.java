package com.billing.invoice.exception_handler.exceptions.bad_request.exceptions;

import com.billing.invoice.exception_handler.exceptions.bad_request.BadRequestException;

import java.time.Month;

public class InvoiceIssuanceException extends BadRequestException {
    public InvoiceIssuanceException(Month month) {
        super(String.format("The invoice was already issued for the month of %s", month));
    }
}
