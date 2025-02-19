package com.billing.invoice.domain.constant;

public enum Discount {

    SERVICE_OVER_12_MONTHS(5),
    SERVICE_OVER_24_MONTHS(10);

    private final int discountPercentage;

    Discount(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }
}
