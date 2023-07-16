package com.example.habitassistant;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class NotitionActivity extends AppCompatActivity {
    public static final String important="important";
    public static final String normal="normal";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        creatNotificationChannels();
    }

    private void creatNotificationChannels(){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

            //通道1设置
            NotificationChannel channel1=new NotificationChannel(
                    important,
                    "important",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("这是重要通道");
            channel1.enableVibration(true); // 可选：允许通知震动

            //通道2设置
            NotificationChannel channel2=new NotificationChannel(
                    normal,
                    "normal",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription("这是普通通道");

            //创建通道
            NotificationManager manager=getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        } else {
            Log.i("MainActivity","版本号小了");
        }
    }




}
