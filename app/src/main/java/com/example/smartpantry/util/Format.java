package com.example.smartpantry.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Formatting and date helpers shared by several screens. */
public final class Format {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final int EXPIRING_SOON_DAYS = 3;

    private Format() { }

    /** 3.0 -> "3", 2.5 -> "2.5" */
    public static String qty(double q) {
        if (q == Math.rint(q)) return String.valueOf((long) q);
        return String.format(Locale.US, "%.2f", q).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    public static String qtyWithUnit(double q, String unit) {
        return qty(q) + (unit == null || unit.isEmpty() ? "" : " " + unit);
    }

    /** Days from today until the given yyyy-MM-dd date (negative = already expired), or null. */
    public static Integer daysUntil(String date) {
        if (date == null || date.isEmpty()) return null;
        try {
            Date d = new SimpleDateFormat(DATE_PATTERN, Locale.US).parse(date);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long diff = d.getTime() - today.getTimeInMillis();
            return (int) Math.round(diff / 86_400_000.0);
        } catch (ParseException e) {
            return null;
        }
    }
}
