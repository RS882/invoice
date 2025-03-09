package com.billing.invoice.services.interfaces;

import com.billing.invoice.constant.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentService {

    void addPayment(Long invoiceId, BigDecimal amount, PaymentMethod method);

    BigDecimal calculateRemainingBalance(Long invoiceId);
}
