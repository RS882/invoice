package com.billing.invoice.services.billing_strategy;

import com.billing.invoice.constant.PlanType;
import com.billing.invoice.exception_handler.exceptions.not_found.excrptions.PlanNotFoundException;
import com.billing.invoice.services.billing_strategy.interfaces.BillingStrategy;

public class BillingStrategyFactory {

    public static BillingStrategy getStrategy(PlanType planType) {
        return switch (planType) {
            case BASIC -> new BasicPlanStrategy();
            case PREMIUM -> new PremiumPlanStrategy();
            case BUSINESS -> new BusinessPlanStrategy();
            default -> throw new PlanNotFoundException(planType);
        };
    }
}
