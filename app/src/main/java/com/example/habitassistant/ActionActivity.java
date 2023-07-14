package com.example.habitassistant;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;


public class ActionActivity extends BroadcastReceiver {

    private static String Tag="RECEIVE CAST";

    private MainActivity mainActivity;

    private String action;
    private String category;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Tag,"context: "+context+" intent: "+intent);

        action=intent.getAction();
        category= intent.getCategories().toString();
        this.context=context;

        System.out.println(action+"   "+category);

        switch (category){
            case "{静音模式}":
                Jingying();
                break;
            case "{应用管理}":
                appController();
                break;
            case "{闹钟管理}":

                break;
            default:
                break;
        }
        NotificationManager manager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra("nid",1));


    }

    private void Jingying() {
        Log.i(Tag, "管理静音模式");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//            if (audioManager != null) {
//                if (Objects.equals(action, "打开")) {
//
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                    //           notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
//
//
//                    Intent intent1=new Intent(String.valueOf(context));
//                    context.startActivity(intent1);
//                } else {
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                    //           notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
//
//                    Intent intent1=new Intent(String.valueOf(context));
//                    context.startActivity(intent1);
//                }
//            }

        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public void appController(){
        Log.i(Tag, "打开应用");

        if (Objects.equals(action, "打开微信")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }




    }



}
