package com.billing.invoice.constant;

import java.math.BigDecimal;

public enum Discount {

    SERVICE_OVER_12_MONTHS(BigDecimal.valueOf(5.00)),
    SERVICE_OVER_24_MONTHS(BigDecimal.valueOf(10.00));

    private final BigDecimal discountPercentage;

    Discount(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
}
