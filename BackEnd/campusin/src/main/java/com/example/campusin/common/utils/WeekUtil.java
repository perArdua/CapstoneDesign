package com.example.campusin.common.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeekUtil {

    public static LocalDate getWeekStartDate(LocalDate date) {
        while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    public static int getWeekOfMonth(LocalDate weekStart) {
        return (weekStart.getDayOfMonth() - 1) / 7 + 1;
    }
}
