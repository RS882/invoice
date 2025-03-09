package com.billing.invoice.services;

import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.domain.entity.PaymentHistory;
import com.billing.invoice.repositories.PaymentHistoryRepository;
import com.billing.invoice.services.interfaces.InvoiceService;
import com.billing.invoice.services.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final InvoiceService invoiceService;

    private final PaymentHistoryRepository repository;

    @Override
    public void addPayment(Long invoiceId, BigDecimal amount, PaymentMethod method) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);

        PaymentHistory newPaymentHistory = PaymentHistory
                .builder()
                .invoice(invoice)
                .paymentMethod(method)
                .amountPaid(amount)
                .build();
        repository.save(newPaymentHistory);

        invoiceService.checkAndUpdateInvoiceStatus(invoiceId);
    }
}
