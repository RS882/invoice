package com.billing.invoice.constant;

import com.billing.invoice.exception_handler.exceptions.not_found.excrptions.PaymentMethodNotFoundException;

public enum PaymentMethod {

    CREDIT_CARD, PAYPAL, BANK_TRANSFER;

    public static PaymentMethod get(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            throw new PaymentMethodNotFoundException(method);
        }
    }

}
