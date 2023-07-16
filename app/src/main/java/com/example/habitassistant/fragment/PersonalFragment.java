package com.example.habitassistant.fragment;

import static com.example.habitassistant.NotitionActivity.important;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.example.habitassistant.ActionActivity;
import com.example.habitassistant.MainActivity;
import com.example.habitassistant.R;

import java.util.Objects;


public class PersonalFragment extends Fragment {
    private int jiaoxuelou_state = 1;
    private int tushuguan_state = 1;
    private int xiuxi_state = 1;
    private int tushuguanyuding_state = 1;
    private int import_state = 1;
    private boolean getin;

    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private Switch switch4;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);

        switch1 = rootView.findViewById(R.id.switch1);
        switch2 = rootView.findViewById(R.id.switch2);
        switch3 = rootView.findViewById(R.id.switch3);
        switch4 = rootView.findViewById(R.id.switch4);
        button = rootView.findViewById(R.id.button);

        //通知
        return rootView;
    }

    public void clickSend1(View view) {
        if (switch1.isChecked()) {
            jiaoxuelou_state = 1;
        } else {
            jiaoxuelou_state = 0;
        }
    }

    public void clickSend2(View view) {
        if (switch2.isChecked()) {
            tushuguan_state = 1;
        } else {
            tushuguan_state = 0;
        }
    }

    public void clickSend3(View view) {
        if (switch3.isChecked()) {
            xiuxi_state = 1;
        } else {
            xiuxi_state = 0;
        }
    }

    public void clickSend4(View view) {
        if (switch4.isChecked()) {
            tushuguanyuding_state = 1;
        } else {
            tushuguanyuding_state = 0;
        }
    }

    public void exitButton(View view) {
        //退出操作
    }
}