package com.billing.invoice.services;

import com.billing.invoice.domain.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.exception_handler.exceptions.not_found.PlanNotFoundException;
import com.billing.invoice.services.billing_strategy.BasicPlanStrategy;
import com.billing.invoice.services.billing_strategy.BusinessPlanStrategy;
import com.billing.invoice.services.billing_strategy.PremiumPlanStrategy;
import com.billing.invoice.services.billing_strategy.interfaces.BillingStrategy;
import com.billing.invoice.services.interfaces.BillingService;
import com.billing.invoice.services.interfaces.CustomerService;
import com.billing.invoice.services.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.billing.invoice.services.billing_strategy.BillingStrategyFactory.getStrategy;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final CustomerService customerService;

    private final InvoiceService invoiceService;

    @Override
    public Invoice generateInvoiceForCustomer(Long customerId) {
        Customer currentCustomer = customerService.getCustomerById(customerId);

        BillingStrategy strategy = getStrategy(currentCustomer.getPlanType());

        Invoice newInvoice = Invoice.builder()
                .customer(currentCustomer)
                .amount(strategy.calculateBill(currentCustomer))
                .build();

        Invoice savedInvoice = invoiceService.saveInvoice(newInvoice);

        return savedInvoice;
    }

    @Override
    public void addPayment(Long invoiceId, BigDecimal amount, PaymentMethod method) {

    }

    @Override
    public BigDecimal calculateRemainingBalance(Long invoiceId) {
        return null;
    }
}
