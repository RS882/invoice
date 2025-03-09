package com.billing.invoice.controllers;

import com.billing.invoice.controllers.APIs.InvoiceAPI;
import com.billing.invoice.domain.dto.payment_dto.InvoiceRemainingBalanceResponseDto;
import com.billing.invoice.services.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
@RestController
@RequiredArgsConstructor
public class InvoiceController implements InvoiceAPI {

    private final InvoiceService invoiceService;

    @Override
    public ResponseEntity<InvoiceRemainingBalanceResponseDto> getRemainingBalance(Long id) {
        BigDecimal remainingBalance = invoiceService.calculateRemainingBalance(id);
        return ResponseEntity.ok(InvoiceRemainingBalanceResponseDto.from(id, remainingBalance));
    }
}
