package com.billing.invoice.exception_handler.exceptions.not_found.excrptions;

import com.billing.invoice.exception_handler.exceptions.not_found.NotFoundException;

public class PaymentMethodNotFoundException extends NotFoundException {
    public PaymentMethodNotFoundException(String method) {
        super(String.format("Payment method <%s> is not found", method));
    }
    public PaymentMethodNotFoundException() {
        super(String.format("Payment method not specified"));
    }
}
