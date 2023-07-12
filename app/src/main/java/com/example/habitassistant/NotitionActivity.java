package com.example.habitassistant;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class NotitionActivity extends Application {
    public static final String CHANNEL_1_ID="channel1";
    public static final String CHANNEL_2_ID="channel2";

    @Override
    public void onCreate() {
        super.onCreate();

        creatNotificationChannels();
    }

    private void creatNotificationChannels(){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

            Log.i("MainActivity","版本号正确");

            NotificationChannel channel1=new NotificationChannel(
                    CHANNEL_1_ID,
                    "channel 1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel1.setDescription("这是通道1");

            NotificationChannel channel2=new NotificationChannel(
                    CHANNEL_2_ID,
                    "channel 2",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription("这是通道2");

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        } else {
            Log.i("MainActivity","版本号小了");
        }
    }


}
