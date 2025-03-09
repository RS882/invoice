package com.billing.invoice.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtilities {

    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static BigDecimal scaleValue(BigDecimal value) {
        return value.setScale(SCALE, ROUNDING_MODE);
    }
}
