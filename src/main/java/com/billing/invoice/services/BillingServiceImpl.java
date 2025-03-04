package com.billing.invoice.services;

import com.billing.invoice.domain.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.services.billing_strategy.BillData;
import com.billing.invoice.services.billing_strategy.interfaces.BillingStrategy;
import com.billing.invoice.services.interfaces.BillingService;
import com.billing.invoice.services.interfaces.CustomerService;
import com.billing.invoice.services.interfaces.DataUsageHistoryService;
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

    private final DataUsageHistoryService dataUsageHistoryService;

    @Override
    public Invoice generateInvoiceForCustomer(Long customerId) {
        Customer currentCustomer = customerService.getCustomerById(customerId);

        checkOfBilling(customerId);

        BillingStrategy strategy = getStrategy(currentCustomer.getPlanType());

        BillData billData = strategy.calculateBill(currentCustomer);

        BigDecimal billAmount = billData.getTotal();

        Invoice savedInvoice = invoiceService.createNewInvoice(currentCustomer, billAmount);

        dataUsageHistoryService.createDataUsageHistory(currentCustomer);

        customerService.cleanDataUsedGB(currentCustomer);

        return savedInvoice;
    }

    private void checkOfBilling(Long customerId) {
        invoiceService.checkInvoiceIssuanceCurrentsMonth(customerId);
        dataUsageHistoryService.checkDataUsageExistencePreviousMonth(customerId);
    }

    @Override
    public void addPayment(Long invoiceId, BigDecimal amount, PaymentMethod method) {

    }

    @Override
    public BigDecimal calculateRemainingBalance(Long invoiceId) {
        return null;
    }
}
