package com.billing.invoice.domain.constant;

public enum Tax {

    VAT(19);

    private final double taxRatePercentage;

    Tax(double taxRate) {
        this.taxRatePercentage = taxRate;
    }

    public double getTaxRatePercentage() {
        return taxRatePercentage;
    }
}
