package com.example.habitassistant.utils;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class UsageTimeHandler {

    private UsageStatsManager usageStatsManager;

    public UsageTimeHandler(Context context) {
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public long getScreenTime(long startTime, long endTime) {
        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
        long totalTime = 0;
        long lastEventTime = 0;
        boolean screenOn = false;

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            if (event.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE) {
                lastEventTime = event.getTimeStamp();
                screenOn = true;
            } else if (screenOn && event.getEventType() == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                totalTime += event.getTimeStamp() - lastEventTime;
                screenOn = false;
            }
        }
        Log.i("UsageTimeHandler", "Fetching screen time");

        return totalTime;
    }

    public SortedMap<Long, UsageStats> getAppUsageStats(long startTime, long endTime) {
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();

        for (UsageStats usageStats : queryUsageStats) {
            sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
        }

        return sortedMap;
    }
}