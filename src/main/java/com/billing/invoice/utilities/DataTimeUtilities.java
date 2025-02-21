package com.billing.invoice.utilities;

import java.time.LocalDate;

public class DataTimeUtilities {

    private static final LocalDate BASIS = LocalDate.now().minusMonths(1);

    public static LocalDate getFirstDayOfLastMonth() {
        return getDayOfBasicMonth(1, BASIS);
    }

    public static LocalDate getLastDayOfLastMonth() {

        int mothNowLength = BASIS.lengthOfMonth();
        return getDayOfBasicMonth(mothNowLength, BASIS);
    }

    private static LocalDate getDayOfBasicMonth(int numOfDay, LocalDate basis) {
        int yearNow = basis.getYear();
        int mothNow = basis.getMonthValue();
        return LocalDate.of(yearNow, mothNow, numOfDay);
    }
}
