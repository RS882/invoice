package com.billing.invoice.logging;

import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.domain.model.InvoiceDataForFile;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GlobalLoggingAspect {

    @Pointcut("execution(* com.billing.invoice.services.billing_strategy..*.calculateBill(..))")
    public void onCalculateBill() {
    }

    @Before("onCalculateBill()")
    public void beforeCalculateBill(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = (args.length > 0 && args[0] instanceof Long) ? (Long) args[0] : null;
        log.info("[{}] Calculating bill for customer: {}", joinPoint.getSignature(), customerId);
    }

    @Pointcut("execution(* com.billing.invoice.services.CustomerServiceImpl.cleanDataUsedGB(..))")
    public void onCleanDataUsedGB() {
    }

    @Before("onCleanDataUsedGB()")
    public void beforeCleanDataUsedGB(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = (args.length > 0 && args[0] instanceof Customer) ? ((Customer) args[0]).getId() : null;
        log.info("[{}] Resetting data usage for customer: {}", joinPoint.getSignature(), customerId);
    }

    @Pointcut("execution(* com.billing.invoice.services.DataUsageHistoryServiceImpl.createDataUsageHistory(..))")
    public void onCreateDataUsageHistory() {
    }

    @Before("onCreateDataUsageHistory()")
    public void beforeCreateDataUsageHistory(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = (args.length > 0 && args[0] instanceof Customer) ? ((Customer) args[0]).getId() : null;
        log.info("[{}] Creating data usage history for customer: {}", joinPoint.getSignature(), customerId);
    }

    @Pointcut("execution(* com.billing.invoice.services.InvoiceServiceImpl.createNewInvoice(..))")
    public void onCreateNewInvoice() {
    }

    @Before("onCreateNewInvoice()")
    public void beforeCreateNewInvoice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = (args.length > 0 && args[0] instanceof Customer) ? ((Customer) args[0]).getId() : null;

        log.info("[{}] Creating new invoice for customer: {}", joinPoint.getSignature(), customerId);
    }

    @Pointcut("execution(* com.billing.invoice.services.BillingServiceImpl.generateInvoiceForCustomer(..))")
    public void onGenerateInvoice() {
    }

    @Before("onGenerateInvoice()")
    public void beforeGenerateInvoice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = args.length > 0 && args[0] instanceof Long ? (Long) args[0] : null;
        log.info("[{}] Generating invoice for customer: {}", joinPoint.getSignature(), customerId);
    }

    @AfterReturning(pointcut = "onGenerateInvoice()", returning = "result")
    public void afterGenerateInvoice(JoinPoint joinPoint, Invoice result) {
        if (result != null) {
            log.info("[{}] Invoice {} generated successfully with total: {}",
                    joinPoint.getSignature(), result.getId(), result.getAmount());
        } else {
            log.warn("[{}] Invoice generation returned null!", joinPoint.getSignature());
        }
    }

    @Pointcut("execution(* com.billing.invoice.services.DataStorageServiceImpl.uploadFile(..))")
    public void onUploadFile() {
    }

    @Before("onUploadFile()")
    public void beforeUploadFile(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = args.length > 0 && args[1] instanceof Long ? (Long) args[1] : null;
        InvoiceDataForFile invoiceData = (args.length > 0 && args[0] instanceof InvoiceDataForFile) ? ((InvoiceDataForFile) args[0]) : null;
        if (invoiceData != null) {
            log.info("[{}] Uploading invoice {} for customer {}", joinPoint.getSignature(), invoiceData.getInvoiceNumber(), customerId);
        } else {
            log.info("[{}] Uploading invoice for customer {}", joinPoint.getSignature(), customerId);
        }
    }

    @AfterReturning("onUploadFile()")
    public void afterUploadFile(JoinPoint joinPoint, String result) {
        if (result != null) {
            Object[] args = joinPoint.getArgs();
            Long invoiceNumber = (args.length > 0 && args[0] instanceof InvoiceDataForFile) ? ((InvoiceDataForFile) args[0]).getInvoiceNumber() : null;
            log.info("[{}] Invoice {} uploaded successfully: {}", joinPoint.getSignature(), invoiceNumber, result);

        } else {
            log.warn("[{}] Upload file returned null!", joinPoint.getSignature());
        }
    }

    @AfterThrowing(pointcut = "onUploadFile()", throwing = "e")
    public void afterThrowingUploadFile(JoinPoint joinPoint, Exception e) {
        log.error("[{}] I/O error while generating or uploading PDF", joinPoint.getSignature(), e);
    }
}

