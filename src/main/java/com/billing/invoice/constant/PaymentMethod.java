package com.billing.invoice.constant;

import com.billing.invoice.exception_handler.exceptions.not_found.excrptions.PaymentMethodNotFoundException;
import lombok.Getter;

public enum PaymentMethod {

    CREDIT_CARD("Credit card"),
    PAYPAL("PayPal"),
    BANK_TRANSFER("Bank transfer");

    @Getter
    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public static PaymentMethod get(String method) {
        if (method == null || method.isBlank()) {
            throw new PaymentMethodNotFoundException();
        }
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PaymentMethodNotFoundException(method);
        }
    }

}
