package com.example.habitassistant;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ActionActivity extends BroadcastReceiver {

    private static String Tag="RECEIVE CAST";

    //
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
        Log.i(Tag,"管理静音模式");

//        AudioManager audioManager = (AudioManager) getSystemService(context,MainActivity.AUDIO_SERVICE);

//        if (audioManager != null) {
//            if (Objects.equals(action, "open")){
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            }else{
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            }
//        }
    }



}
