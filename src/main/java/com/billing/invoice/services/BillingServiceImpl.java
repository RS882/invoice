package com.billing.invoice.services;

import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.domain.model.BillData;
import com.billing.invoice.domain.model.InvoiceDataForFile;
import com.billing.invoice.services.billing_strategy.interfaces.BillingStrategy;
import com.billing.invoice.services.interfaces.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.billing.invoice.services.billing_strategy.BillingStrategyFactory.getStrategy;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private final DataUsageHistoryService dataUsageHistoryService;
    private final DataStorageService dataStorageService;
    private final ApplicationContext applicationContext;

    @Override
    @Transactional
    public Invoice generateInvoiceForCustomer(Long customerId) {

        Customer currentCustomer = customerService.getCustomerById(customerId);
        validateBillingConditions(customerId);

        BillingStrategy strategy = applicationContext.getBean(
                getStrategy(currentCustomer.getPlanType())
                        .getClass());
        BillData billData = strategy.calculateBill(currentCustomer);
        BigDecimal billAmount = billData.getTotal() != null ? billData.getTotal() : BigDecimal.ZERO;

        Invoice savedInvoice = invoiceService.createNewInvoice(currentCustomer, billAmount);

        InvoiceDataForFile invoiceData = InvoiceDataForFile.builder()
                .invoiceNumber(savedInvoice.getId())
                .invoiceDate(savedInvoice.getBillingDate())
                .billData(billData)
                .build();

        String invoicePath = dataStorageService.uploadFile(invoiceData, customerId);
        savedInvoice.setInvoiceFilePath(invoicePath);
        invoiceService.saveInvoice(savedInvoice);

        dataUsageHistoryService.createDataUsageHistory(currentCustomer);

        customerService.cleanDataUsedGB(currentCustomer);

        return savedInvoice;
    }

    private void validateBillingConditions(Long customerId) {
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
