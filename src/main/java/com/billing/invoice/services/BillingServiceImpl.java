package com.billing.invoice.services;

import com.billing.invoice.domain.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.services.interfaces.BillingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BillingServiceImpl implements BillingService {
    @Override
    public Invoice generateInvoiceForCustomer(Long customerId) {
        return null;
    }

    @Override
    public void addPayment(Long invoiceId, BigDecimal amount, PaymentMethod method) {

    }

    @Override
    public BigDecimal calculateRemainingBalance(Long invoiceId) {
        return null;
    }
}
