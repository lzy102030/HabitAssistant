package com.example.habitassistant;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;


public class ActionActivity extends BroadcastReceiver {

    private static String Tag="RECEIVE CAST";
    private String action;
    private String category;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Tag, "context: " + context + " intent: " + intent);

        action = intent.getAction();
        category = intent.getCategories().toString();
        this.context = context;

        System.out.println(action + "   " + category);

        switch (category) {
            case "{应用管理}":
                appController();
                break;
            case "{勿扰模式}":
                wuraoController();
                break;
            default:
                break;
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra("nid", 0));
    }

//备用方法
//    private void Jingying() {
//        Log.i(Tag, "管理静音模式");
//
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        if (audioManager != null) {
//            if (Objects.equals(action, "打开")){
//                //开启静音模式
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                Log.i("MainActivity","开启静音模式");
//            }else {
//                //关闭静音模式
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                Log.i("MainActivity","关闭静音模式");
//            }
//
//        }else {
//            Log.i("MainActivity","静音模式控制失败");
//        }
//
//
//    }

    private void appController(){
        Log.i(Tag, "打开应用");

        switch (action){
            case "打开微信":
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            break;
            case "打开音乐": {
                Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.parse("file:///path/to/your/music/file"), "audio/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

                if (!activities.isEmpty()) {
                    // 创建选择器Intent
                    Intent chooserIntent = Intent.createChooser(intent, "选择音乐播放器");
                    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(chooserIntent);
                } else {
                    // 没有找到支持的音乐播放器应用程序
                    Log.i("MainActivity","没有相应程序");
                }
            }
                break;

            default:
                Log.i("MainActivity","崩坏三，启动");
                break;
            }
    }


    private void wuraoController(){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                // 如果没有权限，请求授权
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Log.i("MainActivity","勿扰模式申请权限");
            } else {
                if (Objects.equals(action, "打开")){
                    // 开启勿扰模式
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                    //Log.i("MainActivity","勿扰模式开启成功");
                }else {
                    //关闭勿扰模式
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    //Log.i("MainActivity","勿扰模式关闭成功");
                }
            }
        }else {
            Log.i("MainActivity","勿扰模式控制失败");
        }
    }



}
