package com.billing.invoice.services.billing_strategy;

import com.billing.invoice.domain.constant.PlanType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BasicPlanStrategy extends AbstractBillingStrategy {

    public BasicPlanStrategy() {
        super(PlanType.BASIC);
    }
}
