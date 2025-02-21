package com.billing.invoice.exception_handler.exceptions.not_found.excrptions;

import com.billing.invoice.domain.constant.PlanType;
import com.billing.invoice.exception_handler.exceptions.not_found.NotFoundException;

public class PlanNotFoundException extends NotFoundException {

    public PlanNotFoundException(PlanType plan) {
        super(String.format("Plan <%s> not found", plan.name()));
    }
}
