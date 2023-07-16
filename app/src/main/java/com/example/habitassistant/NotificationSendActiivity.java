package com.example.habitassistant;

import static com.example.habitassistant.NotitionActivity.important;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;


public class NotificationSendActiivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManagerCompat;
    private Intent intent;

    private String info_old;
    private String info_new;

    private int jiaoxuelou_state=1;
    private int tushuguan_state=1;
    private int xiuxi_state=1;
    private int tushuguanyuding_state=1;
    private int import_state=1;
    private boolean getin;

    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private Switch switch4;
    private Button button;


    /*
    2个通道：important与normal，前者有弹窗与震动，后者只会显示在通知栏里
    通知的id分配：
    1-9：勿扰模式的开与关

    10-99：应用的开与关

    */

    private static int wurao_nid=1;
    private static int vx_nid=10;
    private static int music_nid=20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_personal);
        switch1=findViewById(R.id.switch1);
        switch2=findViewById(R.id.switch2);
        switch3=findViewById(R.id.switch3);
        switch4=findViewById(R.id.switch4);
        button=findViewById(R.id.button);

        //检查按钮状态
        if (switch1.isChecked()){
                jiaoxuelou_state=1;
        }else {
                jiaoxuelou_state=0;
        }
        if (switch2.isChecked()){
                tushuguan_state=1;
        }else {
                tushuguan_state=0;
        }
        if (switch3.isChecked()){
                xiuxi_state=1;
        }else {
                xiuxi_state=0;
        }
        if (switch4.isChecked()){
                tushuguanyuding_state=1;
        }else {
                tushuguanyuding_state=0;
        }


        //通知
        notificationManagerCompat=NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pemissioncheck();
        }

        intent=getIntent();
        info_new=intent.getStringExtra("info");

        System.out.println(info_old);
        System.out.println(info_new);

        if (!Objects.equals(info_new, info_old)){

            //进入教学楼
            if (info_new.contains( "教学楼")) {
                getin = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            //出教学楼
            if (info_old!=null&&info_old.contains("教学楼")){
                getin=false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            //进入
            if (info_new.contains( "图书馆")){
                getin=false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            //出
            if (info_old!=null&&info_old.contains( "图书馆")){
                getin=false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            info_old=info_new;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pemissioncheck(){
        //勿扰模式权限
        boolean areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!areNotificationsEnabled) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
            this.startActivity(intent);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void n_wurao() {

        if (jiaoxuelou_state == 1) {
                Intent intent_Main = new Intent(this, MainActivity.class);
                Intent intent_Action1 = new Intent(this, ActionActivity.class);
                Intent intent_Action2 = new Intent(this, ActionActivity.class);

//                intent_Action1.setAction("打开");
//                intent_Action1.addCategory("勿扰模式");
//                intent_Action1.putExtra("nid", wurao_nid);
//                intent_Action1.setPackage(String.valueOf(this));

                intent_Action1.setAction("打开");
                intent_Action1.addCategory("勿扰模式");

                if (getin==true){
                    //开启勿扰
                    sendBroadcast(intent_Action1);
                }

                intent_Action1.putExtra("nid", wurao_nid);
                intent_Action1.setPackage(String.valueOf(this));

                intent_Action2.setAction("关闭");
                intent_Action2.addCategory("勿扰模式");

                if (getin!=true){
                    //关闭勿扰
                    sendBroadcast(intent_Action2);
                }

                intent_Action2.putExtra("nid", wurao_nid);
                intent_Action2.setPackage(String.valueOf(this));

                //转到主页
                PendingIntent pending_Main = PendingIntent.getActivity(this, 0,
                        intent_Main, PendingIntent.FLAG_MUTABLE);
                //转到通知
                PendingIntent pending_Action1 = PendingIntent.getBroadcast(this, wurao_nid,
                        intent_Action1, PendingIntent.FLAG_MUTABLE);
                PendingIntent pending_Action2 = PendingIntent.getBroadcast(this, wurao_nid,
                        intent_Action2, PendingIntent.FLAG_MUTABLE);

                //震动时长设置
                long[] vibrationPattern = {500, 500, 500, 500};

                if (getin==true) {
                    //通知内容
                    Notification notification = new NotificationCompat.Builder(this, important)
                            .setSmallIcon(R.drawable.baseline_smartphone_24)
                            .setContentTitle("自动打开勿扰模式")
                            .setContentText("您已进入教学区，已帮您打开勿扰模式")
                            .setVibrate(vibrationPattern)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setAutoCancel(true)
                            .setContentIntent(pending_Main)
//                        .addAction(0, "去打开", pending_Action1)
                            .addAction(0, "去关闭", pending_Action2)
                            .setWhen(System.currentTimeMillis())
                            .setGroup("myGroup")
                            .build();
                    //显示通知
                    notificationManagerCompat.notify(wurao_nid, notification);

                }else if (getin==false){
                    Notification notification = new NotificationCompat.Builder(this, important)
                            .setSmallIcon(R.drawable.baseline_smartphone_24)
                            .setContentTitle("自动关闭勿扰模式")
                            .setContentText("您已离开教学区，已帮您关闭勿扰模式")
                            .setVibrate(vibrationPattern)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setAutoCancel(true)
                            .setContentIntent(pending_Main)
                            .addAction(0, "去打开", pending_Action1)
//                        .addAction(0, "去关闭", pending_Action2)
                            .setWhen(System.currentTimeMillis())
                            .setGroup("myGroup")
                            .build();
                    //显示通知
                    notificationManagerCompat.notify(wurao_nid, notification);
                }
            }

        else if (jiaoxuelou_state == 0){
            Log.i("MainActivity","已关闭该通知");
        }
    }

    public void n_openvx(){
        //        Log.i("MainActivity","提醒按钮1被点击");

        Intent intent_Main = new Intent(this, MainActivity.class);
        Intent intent_Action1 = new Intent(this,ActionActivity.class);

        intent_Action1.setAction("打开微信");
        intent_Action1.addCategory("应用管理");
        intent_Action1.putExtra("nid",vx_nid);
        intent_Action1.setPackage(String.valueOf(this));

        //转到主页
        PendingIntent pending_Main=PendingIntent.getActivity(this, 0,
                intent_Main, PendingIntent.FLAG_MUTABLE);
        //覆盖前一个通知
        PendingIntent pending_Action1=PendingIntent.getBroadcast(this, vx_nid,
                intent_Action1, PendingIntent.FLAG_MUTABLE);


        //震动时长设置
        long[] vibrationPattern = {500, 500, 500, 500};

        //通知内容
        Notification notification=new NotificationCompat.Builder(this,important)
                .setSmallIcon(R.drawable.baseline_smartphone_24)
                .setContentTitle("定时提醒")
                .setContentText("已经到了图书馆预订位置时间，请打开微信去预订座位")
                .setVibrate(vibrationPattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pending_Main)
                .addAction(0,"去打开",pending_Action1)
                .setWhen(System.currentTimeMillis())
                .build();

        //显示通知
        notificationManagerCompat.notify(vx_nid,notification);
    }

    public void n_openmusic(){
        //        Log.i("MainActivity","提醒按钮1被点击");

        Intent intent_Main = new Intent(this, MainActivity.class);
        Intent intent_Action1 = new Intent(this,ActionActivity.class);

        intent_Action1.setAction("打开音乐");
        intent_Action1.addCategory("应用管理");
        intent_Action1.putExtra("nid",music_nid);
        intent_Action1.setPackage(String.valueOf(this));

        //转到主页
        PendingIntent pending_Main=PendingIntent.getActivity(this, 0,
                intent_Main, PendingIntent.FLAG_MUTABLE);
        PendingIntent pending_Action1=PendingIntent.getBroadcast(this, music_nid,
                intent_Action1, PendingIntent.FLAG_MUTABLE);

        //震动时长设置
        long[] vibrationPattern = {500, 500, 500, 500};

        //通知内容
        Notification notification=new NotificationCompat.Builder(this,important)
                .setSmallIcon(R.drawable.baseline_smartphone_24)
                .setContentTitle("休息提醒")
                .setContentText("你已经很辛苦了，请听点音乐，放松一下吧")
                .setVibrate(vibrationPattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pending_Main)
                .addAction(0,"放松",pending_Action1)
                .setWhen(System.currentTimeMillis())
                .setGroup("myGroup")
                .build();

        //显示通知
        notificationManagerCompat.notify(music_nid,notification);
    }

    public void click_send1(View view){
        if (switch1.isChecked()){
            jiaoxuelou_state=1;
        }else {
            jiaoxuelou_state=0;
        }
    }
    public void click_send2(View view){
        if (switch2.isChecked()){
            tushuguan_state=1;
        }else {
            tushuguan_state=0;
        }
    }
    public void click_send3(View view){
        if (switch3.isChecked()){
            xiuxi_state=1;
        }else {
            xiuxi_state=0;
        }
    }
    public void click_send4(View view){
        if (switch4.isChecked()){
            tushuguanyuding_state=1;
        }else {
            tushuguanyuding_state=0;
        }
    }

    public void exit_button(View view){


    }




}
