package com.billing.invoice.services.interfaces;

import com.billing.invoice.domain.model.InvoiceDataForFile;

import java.io.InputStream;

public interface PdfGeneratorService {

     InputStream generatePdf(InvoiceDataForFile data);
}
