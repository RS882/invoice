package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;

import java.math.BigDecimal;

public interface InvoiceService {

    Invoice createNewInvoice(Customer customer, BigDecimal amount);

    void checkInvoiceIssuanceCurrentsMonth(Long customerId);

    Invoice getInvoiceById(Long id);

    BigDecimal calculateRemainingBalance(Long invoiceId);

    Invoice updateInvoiceFilePath(Invoice invoice, String invoiceFilePath);

    void checkAndUpdateInvoiceStatus(Long invoiceId);
}
