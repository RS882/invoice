package com.billing.invoice.services;

import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.domain.model.InvoiceDataForFile;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.domain.model.BillData;
import com.billing.invoice.exception_handler.exceptions.server_exception.ServerIOException;
import com.billing.invoice.services.billing_strategy.interfaces.BillingStrategy;
import com.billing.invoice.services.interfaces.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static com.billing.invoice.services.billing_strategy.BillingStrategyFactory.getStrategy;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private final DataUsageHistoryService dataUsageHistoryService;
    private final DataStorageService dataStorageService;

    @Override
    @Transactional
    public Invoice generateInvoiceForCustomer(Long customerId) {
        log.info("Generating invoice for customer: {}", customerId);

        Customer currentCustomer = customerService.getCustomerById(customerId);
        validateBillingConditions(customerId);

        log.info("Calculating bill for customer: {}", customerId);
        BillingStrategy strategy = getStrategy(currentCustomer.getPlanType());
        BillData billData = strategy.calculateBill(currentCustomer);
        BigDecimal billAmount = billData.getTotal() != null ? billData.getTotal() : BigDecimal.ZERO;

        log.info("Creating new invoice for customer: {}", customerId);
        Invoice savedInvoice = invoiceService.createNewInvoice(currentCustomer, billAmount);

        InvoiceDataForFile invoiceData = InvoiceDataForFile.builder()
                .invoiceNumber(savedInvoice.getId())
                .invoiceDate(savedInvoice.getBillingDate())
                .billData(billData)
                .build();

        try {
            log.info("Uploading invoice {} for customer {}", savedInvoice.getId(), customerId);

            String invoicePath = dataStorageService.uploadFile(invoiceData, customerId);
            savedInvoice.setInvoiceFilePath(invoicePath);
            invoiceService.saveInvoice(savedInvoice);

            log.info("Invoice {} uploaded successfully: {}", savedInvoice.getId(), invoicePath);
        } catch (Exception e) {
            log.error("Error uploading invoice PDF for customer {}: {}", customerId, e.getMessage(), e);
            throw new ServerIOException("Failed to upload invoice PDF : "+ e.getMessage());
        }

        log.info("Creating data usage history for customer: {}", customerId);
        dataUsageHistoryService.createDataUsageHistory(currentCustomer);

        log.info("Resetting data usage for customer: {}", customerId);
        customerService.cleanDataUsedGB(currentCustomer);

        log.info("Invoice {} generated successfully with total: {}", savedInvoice.getId(), billAmount);

        return savedInvoice;
    }

    private void validateBillingConditions(Long customerId) {
        log.info("Checking billing conditions for customer: {}", customerId);
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
