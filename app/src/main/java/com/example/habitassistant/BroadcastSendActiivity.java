package com.example.habitassistant;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;


public class BroadcastSendActiivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initSend();
    }

    private void initSend() {
        Intent intent=new Intent();

    }
}