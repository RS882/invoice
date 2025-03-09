package com.billing.invoice.services.interfaces;

import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Invoice;

import java.math.BigDecimal;

public interface BillingService {

    Invoice generateInvoiceForCustomer(Long customerId);
}
