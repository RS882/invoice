package com.billing.invoice.services.billing_strategy;

import com.billing.invoice.constant.Discount;
import com.billing.invoice.constant.PlanType;
import com.billing.invoice.constant.Tax;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.model.BillData;
import com.billing.invoice.services.billing_strategy.interfaces.BillingStrategy;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.billing.invoice.utilities.BigDecimalUtilities.*;

@Slf4j
public abstract class AbstractBillingStrategy implements BillingStrategy {

    protected final BigDecimal price;
    protected final int limit;
    protected final BigDecimal overageCharge;
    protected final PlanType plan;
    protected final BigDecimal vatRatePercentage;

    public AbstractBillingStrategy(@NonNull PlanType plan) {
        this.price = scaleValue(Objects.requireNonNull(plan.getPrice(), "Price cannot be null"));
        this.limit = plan.getLimit();
        this.overageCharge = scaleValue(Objects.requireNonNull(plan.getOverageCharge(), "Overage charge cannot be null"));
        this.plan = plan;
        this.vatRatePercentage = scaleValue(Objects.requireNonNull(Tax.VAT.getTaxRatePercentage(), "VAT cannot be null"));
    }

    @Override
    public BillData calculateBill(@NonNull Customer customer) {
        BigDecimal extraGBPrice = calculateOverageCharge(customer.getDataUsedGB());
        BigDecimal totalBeforeDiscount = scaleValue(price.add(extraGBPrice));

        BigDecimal discountRate = getDiscountRate(customer.getMonthsSubscribed());
        BigDecimal discount = getDiscount(discountRate, totalBeforeDiscount);
        BigDecimal totalAfterDiscount = scaleValue(totalBeforeDiscount.subtract(discount));

        BigDecimal vat = calculateVat(totalAfterDiscount);
        BigDecimal total = scaleValue(totalAfterDiscount.add(vat));

        return BillData.builder()
                .plan(plan)
                .price(price)
                .limit(limit)
                .overageCharge(overageCharge)
                .extraGBPrice(extraGBPrice)
                .discountRate(discountRate)
                .discount(discount)
                .vatRate(vatRatePercentage)
                .vat(vat)
                .total(total)
                .build();
    }

    private BigDecimal calculateVat(BigDecimal totalAfterDiscount) {
        return scaleValue(totalAfterDiscount
                .multiply(vatRatePercentage)
                .divide(ONE_HUNDRED, SCALE, ROUNDING_MODE));
    }

    private BigDecimal calculateOverageCharge(double dataUsedGB) {
        double extraGB = Math.max(0, dataUsedGB - limit);
        return scaleValue(BigDecimal.valueOf(extraGB).multiply(overageCharge));
    }

    private BigDecimal getDiscountRate(int monthsSubscribed) {
        if (monthsSubscribed <= 12) {
            return BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);
        }
        Discount discountRate = monthsSubscribed > 24
                ? Discount.SERVICE_OVER_24_MONTHS
                : Discount.SERVICE_OVER_12_MONTHS;

        return scaleValue(discountRate.getDiscountPercentage());
    }

    private BigDecimal getDiscount(BigDecimal discountRate, BigDecimal totalBeforeDiscount) {
        if (discountRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);
        }
        return scaleValue(totalBeforeDiscount
                .multiply(discountRate)
                .divide(ONE_HUNDRED, SCALE, ROUNDING_MODE));
    }
}