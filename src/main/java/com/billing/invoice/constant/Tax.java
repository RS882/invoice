package com.billing.invoice.constant;

import java.math.BigDecimal;

public enum Tax {

    VAT(BigDecimal.valueOf(19.00));

    private final BigDecimal taxRatePercentage;

    Tax(BigDecimal taxRate) {
        this.taxRatePercentage = taxRate;
    }

    public BigDecimal getTaxRatePercentage() {
        return taxRatePercentage;
    }
}
