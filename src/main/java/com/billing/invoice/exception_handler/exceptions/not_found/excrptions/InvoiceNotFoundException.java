package com.billing.invoice.exception_handler.exceptions.not_found.excrptions;

import com.billing.invoice.exception_handler.exceptions.not_found.NotFoundException;

public class InvoiceNotFoundException extends NotFoundException {
    public InvoiceNotFoundException(Long id) {
        super(String.format("Invoice with id <%d> not found", id));
    }
}
