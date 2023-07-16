package com.example.habitassistant.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public static float getHour(int minutes) {
        return (float) minutes / 3600;
    }

    public static String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date currentTime = Calendar.getInstance().getTime();
        return sdf.format(currentTime);
    }
    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = Calendar.getInstance().getTime();
        return sdf.format(currentDate);
    }
}
