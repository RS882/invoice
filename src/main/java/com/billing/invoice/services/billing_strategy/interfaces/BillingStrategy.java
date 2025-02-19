package com.billing.invoice.services.billing_strategy.interfaces;

import com.billing.invoice.domain.entity.Customer;

import java.math.BigDecimal;

public interface BillingStrategy {

    BigDecimal calculateBill(Customer customer);
}
