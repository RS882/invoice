package com.billing.invoice.services.billing_strategy;

import com.billing.invoice.domain.constant.PlanType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class InvoiceData {

    PlanType plan;

    BigDecimal price;

    int limit;

    BigDecimal overageCharge;

    BigDecimal extraGBPrice;

    BigDecimal discountRate;

    BigDecimal discount;

    BigDecimal vat;

    BigDecimal total;
}
