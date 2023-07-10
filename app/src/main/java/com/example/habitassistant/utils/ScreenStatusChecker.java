package com.example.habitassistant.utils;

import android.content.Context;
import android.os.PowerManager;

public class ScreenStatusChecker {

    private Context context;

    public ScreenStatusChecker(Context context) {
        this.context = context;
    }

    public boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            // 对于API 20及以上版本
            return powerManager.isInteractive();
        } else {
            // 对于API 20以下版本
            return powerManager.isScreenOn();
        }
    }
}