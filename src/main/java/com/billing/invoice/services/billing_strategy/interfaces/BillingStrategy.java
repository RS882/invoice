package com.billing.invoice.services.billing_strategy.interfaces;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.services.billing_strategy.InvoiceData;

import java.math.BigDecimal;

public interface BillingStrategy {

    BigDecimal calculateBill(Customer customer);

    InvoiceData getDataForInvoiceFile(Customer customer);

    void clearInvoiceCache(Long customerId);
}
