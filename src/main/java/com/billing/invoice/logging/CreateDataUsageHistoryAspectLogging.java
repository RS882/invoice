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
public class CreateDataUsageHistoryAspectLogging {

    @Pointcut("execution(* com.billing.invoice.services.DataUsageHistoryServiceImpl.createDataUsageHistory(..))")
    public void onCreateDataUsageHistory() {
    }

    @Before("onCreateDataUsageHistory()")
    public void beforeCreateDataUsageHistory(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = (args.length > 0 && args[0] instanceof Customer) ? ((Customer) args[0]).getId() : null;
        log.info("[{}] Creating data usage history for customer: {}", joinPoint.getSignature(), customerId);
    }
}
