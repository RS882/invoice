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

    public static LocalDate getFirstDayOfLastMonth(LocalDate basic) {
        return getDayOfBasicMonth(1, basic);
    }

    public static LocalDate getLastDayOfLastMonth(LocalDate basic) {
        int mothNowLength = basic.lengthOfMonth();
        return getDayOfBasicMonth(mothNowLength, basic);
    }

    private static LocalDate getDayOfBasicMonth(int numOfDay, LocalDate basis) {
        int yearNow = basis.getYear();
        int mothNow = basis.getMonthValue();
        return LocalDate.of(yearNow, mothNow, numOfDay);
    }
}
