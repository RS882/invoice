package com.billing.invoice.services.billing_strategy;

import com.billing.invoice.domain.constant.Discount;
import com.billing.invoice.domain.constant.PlanType;
import com.billing.invoice.domain.constant.Tax;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.services.billing_strategy.interfaces.BillingStrategy;

import java.math.BigDecimal;

public abstract class AbstractBillingStrategy implements BillingStrategy {

    protected BigDecimal prise;

    protected int limit;

    protected BigDecimal overageCharge;

    public AbstractBillingStrategy(PlanType plan) {
        this.prise = plan.getPrise();
        this.limit = plan.getLimit();
        this.overageCharge = plan.getOverageCharge();
    }

    @Override
    public BigDecimal calculateBill(Customer customer) {

        double extraGB = Math.max(0, customer.getDataUsedGB() - limit);

        BigDecimal extraGBPrise = BigDecimal.valueOf(extraGB).multiply(overageCharge);

        BigDecimal totalBeforeDiscount = prise.add(extraGBPrise);

        BigDecimal totalAfterDiscount = getTotalAfterDiscount(customer.getMonthsSubscribed(), totalBeforeDiscount);

        BigDecimal vat = totalAfterDiscount.multiply(BigDecimal.valueOf(Tax.VAT.getTaxRatePercentage()));

        return totalAfterDiscount.add(vat);
    }

    private static BigDecimal getTotalAfterDiscount(int monthsSubscribed, BigDecimal totalBeforeDiscount) {

        if (monthsSubscribed <= 12) return totalBeforeDiscount;

        Discount discountRate = monthsSubscribed > 24 ? Discount.SERVICE_OVER_24_MONTHS : Discount.SERVICE_OVER_12_MONTHS;

        BigDecimal discount = totalBeforeDiscount.multiply(
                BigDecimal.valueOf(discountRate.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)
        ).setScale(2, BigDecimal.ROUND_HALF_UP);

        return totalBeforeDiscount.subtract(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}