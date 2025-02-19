package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;

import java.math.BigDecimal;

public interface BillingService {

    Invoice generateInvoiceForCustomer(Long customerId);

    void addPayment(Long invoiceId, BigDecimal amount, PaymentMethod method);

    BigDecimal calculateRemainingBalance(Long invoiceId);
}
