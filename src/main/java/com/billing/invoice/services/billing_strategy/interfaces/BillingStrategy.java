package com.billing.invoice.services.billing_strategy.interfaces;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.services.billing_strategy.BillData;

public interface BillingStrategy {

    BillData calculateBill(Customer customer);
}
