package com.billing.invoice.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class InvoiceDataForFile {

    long invoiceNumber;

    LocalDate invoiceDate;

    BillData billData;
}
