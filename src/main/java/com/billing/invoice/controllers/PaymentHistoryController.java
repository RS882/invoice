package com.billing.invoice.controllers;

import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.controllers.APIs.PaymentHistoryAPI;
import com.billing.invoice.domain.dto.invoice_dto.ResponseMessageDto;
import com.billing.invoice.domain.dto.payment_dto.PaymentRequestDto;
import com.billing.invoice.services.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static com.billing.invoice.utilities.BigDecimalUtilities.scaleValue;

@RestController
@RequiredArgsConstructor
public class PaymentHistoryController implements PaymentHistoryAPI {

    private final PaymentService paymentService;

    @Override
    public ResponseEntity<ResponseMessageDto> createNewPaymentHistory(PaymentRequestDto dto) {

        paymentService.addPayment(
                dto.getInvoiceId(),
                scaleValue(BigDecimal.valueOf(dto.getAmount())),
                PaymentMethod.get(dto.getMethod()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessageDto("Payment created successful"));
    }
}
