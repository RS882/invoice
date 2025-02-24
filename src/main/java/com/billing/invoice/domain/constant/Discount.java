package com.billing.invoice.domain.constant;

import java.math.BigDecimal;

public enum Discount {

    SERVICE_OVER_12_MONTHS(BigDecimal.valueOf(5)),
    SERVICE_OVER_24_MONTHS(BigDecimal.valueOf(10));

    private final BigDecimal discountPercentage;

    Discount(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
}
