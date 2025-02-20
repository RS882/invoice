package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.entity.Invoice;

public interface InvoiceService {

    Invoice saveInvoice(Invoice invoice);
}
