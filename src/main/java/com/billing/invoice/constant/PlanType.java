package com.billing.invoice.constant;

import java.math.BigDecimal;

public enum PlanType {

    BASIC(new BigDecimal(30), 50, new BigDecimal(2)),
    PREMIUM(new BigDecimal(50), 100, new BigDecimal(1.50)),
    BUSINESS(new BigDecimal(100), 500, new BigDecimal(1));

    private final BigDecimal price;

    private final int limit;

    private final BigDecimal overageCharge;

    PlanType(BigDecimal prise, int limit, BigDecimal overageCharge) {
        this.price = prise;
        this.limit = limit;
        this.overageCharge = overageCharge;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getLimit() {
        return limit;
    }

    public BigDecimal getOverageCharge() {
        return overageCharge;
    }
}


