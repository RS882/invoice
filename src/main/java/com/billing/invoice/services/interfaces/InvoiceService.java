package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;

import java.math.BigDecimal;

public interface InvoiceService {

    Invoice createNewInvoice(Customer customer, BigDecimal amount);

    Invoice saveInvoice(Invoice invoice);

    void checkInvoiceIssuanceCurrentsMonth(Long customerId);
}
