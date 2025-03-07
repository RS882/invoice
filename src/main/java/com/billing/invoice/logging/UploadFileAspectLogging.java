package com.billing.invoice.logging;

import com.billing.invoice.domain.model.InvoiceDataForFile;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class UploadFileAspectLogging {

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
