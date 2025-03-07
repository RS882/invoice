package com.billing.invoice.logging;


import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GenerateInvoiceAspectLogging {

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

}
