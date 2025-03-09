package com.billing.invoice.controllers;

import com.billing.invoice.controllers.APIs.BillingAPI;
import com.billing.invoice.domain.dto.invoice_dto.InvoiceResponseDto;
import com.billing.invoice.domain.dto.payment_dto.InvoiceRemainingBalanceResponseDto;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.services.interfaces.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class BillingController implements BillingAPI {

    private final BillingService billingService;

    @Override
    public ResponseEntity<InvoiceResponseDto> getInvoiceForCustomer(Long id) {
        Invoice invoice = billingService.generateInvoiceForCustomer(id);
        return ResponseEntity.ok(InvoiceResponseDto.from(invoice));
    }
}
