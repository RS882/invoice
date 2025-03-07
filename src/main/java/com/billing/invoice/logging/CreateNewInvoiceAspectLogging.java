package com.billing.invoice.logging;

import com.billing.invoice.domain.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CreateNewInvoiceAspectLogging {

    @Pointcut("execution(* com.billing.invoice.services.InvoiceServiceImpl.createNewInvoice(..))")
    public void onCreateNewInvoice() {
    }

    @Before("onCreateNewInvoice()")
    public void beforeCreateNewInvoice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = (args.length > 0 && args[0] instanceof Customer) ? ((Customer) args[0]).getId() : null;

        log.info("[{}] Creating new invoice for customer: {}", joinPoint.getSignature(),customerId);
    }
}
