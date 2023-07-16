package com.example.habitassistant.utils;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeConverter {
    public static float convertTime(String iso8601Str) {
        // 使用DateTimeFormatter的ISO_LOCAL_DATE_TIME静态字段来解析ISO 8601格式的日期时间字符串
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.parse(iso8601Str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        // 提取时、分
        int hour = 0;
        int minute = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hour = dateTime.getHour();
            minute = dateTime.getMinute();
        }

        float timeInFloat = hour + minute / 60.0f;

        return (float) (Math.round(timeInFloat * 10) / 10.0);
    }

    public static Date convertTimeToDate(String iso8601Str) {
        // 使用DateTimeFormatter的ISO_LOCAL_DATE_TIME静态字段来解析ISO 8601格式的日期时间字符串
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.parse(iso8601Str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        // 提取时、分
        int year=0;
        int month=0;
        int day=0;
        int hour = 0;
        int minute = 0;
        int second=0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            year=dateTime.getYear();
            month=dateTime.getMonthValue();
            day=dateTime.getDayOfMonth();

            hour = dateTime.getHour();
            minute = dateTime.getMinute();
            second=dateTime.getSecond();
        }

        Date date=new Date();
        date.setYear(year-1);
        date.setMonth(month-1);
        date.setDate(day);
        date.setHours(hour);
        date.setMinutes(minute);
        date.setSeconds(second);

        return date;
    }

    public static int getDay(String iso8601Str) {
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.parse(iso8601Str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        int year = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            year = dateTime.getYear();
        }

        return 1;
    }
}
