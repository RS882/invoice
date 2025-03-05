package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.model.InvoiceDataForFile;

public interface DataStorageService {

    String uploadFile(InvoiceDataForFile data, Long customerId);
}
