package com.billing.invoice.domain.model;

import com.billing.invoice.constant.PlanType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class BillData {

    PlanType plan;

    BigDecimal price;

    int limit;

    BigDecimal overageCharge;

    BigDecimal extraGBPrice;

    BigDecimal discountRate;

    BigDecimal discount;

    BigDecimal vatRate;

    BigDecimal vat;

    BigDecimal total;

}
