package com.billing.invoice.services.billing_strategy;

import com.billing.invoice.constant.PlanType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PremiumPlanStrategy extends AbstractBillingStrategy{

    public PremiumPlanStrategy() {
        super(PlanType.PREMIUM);
    }
}