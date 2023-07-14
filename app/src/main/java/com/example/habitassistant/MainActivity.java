package com.example.habitassistant;

import static android.content.ContentValues.TAG;

import static com.example.habitassistant.NotitionActivity.important;
import static com.example.habitassistant.NotitionActivity.normal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.habitassistant.utils.ScreenStatusChecker;
import com.example.habitassistant.utils.SensorHandler;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorHandler.SensorDataListener{

    private SensorHandler sensorHandler;
    private ScreenStatusChecker screenStatusChecker;
    private Handler handler = new Handler();
    private double gyroscopeX;
    private double gyroscopeY;
    private double gyroscopeZ;
    private double accelerX;
    private double accelerY;
    private double accelerZ;
    private boolean screenStatus;

    private Button btn_noti;

    private String weather;
    private String windScale;
    private String windSpeed;
    private String latitude;
    private String longitude;
    private LocationManager locationManager;

    private NotificationManagerCompat notificationManagerCompat;

    private NotitionActivity notitionActivity;
    boolean areNotificationsEnabled;

    private int nid=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_noti= findViewById(R.id.btn_mes);

        //通知
        notificationManagerCompat=NotificationManagerCompat.from(this);

        //传感器
        sensorHandler = new SensorHandler(this, this);
        //屏幕是否亮
        screenStatusChecker = new ScreenStatusChecker(this);
        //位置传感器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //连接天气API
        HeConfig.init("HE2307051114281793", "8055206813554fa99ce7e0a115b15683");
        //切换至免费订阅
        HeConfig.switchToDevService();
    }

    //传感器
    @Override
    protected void onResume() {
        super.onResume();
        sensorHandler.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHandler.unregister();
    }

    @Override
    public void onGyroscopeDataChanged(float x, float y, float z) {
        //System.out.println(String.format("Gyroscope - x: %.2f, y: %.2f, z: %.2f", x, y, z));
        gyroscopeX = x;
        gyroscopeY = y;
        gyroscopeZ = z;
    }

    @Override
    public void onAccelerometerDataChanged(float x, float y, float z) {
        //System.out.println(String.format("Accelerometer - x: %.2f, y: %.2f, z: %.2f", x, y, z));
        accelerX = x;
        accelerY = y;
        accelerZ = z;
    }


    private void putTable() {
        // 这个 Runnable 对象定义了每10秒执行一次的代码
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                screenStatus = screenStatusChecker.isScreenOn();
                getGPS();
                getTheater();
                // 检查并打印屏幕状态
//                if (screenStatusChecker.isScreenOn()) {
//                    Log.i("ScreenStatus", String.valueOf(screenStatus));
//                } else {
//                    Log.i("ScreenStatus", "Screen is OFF");
//                }

                //陀螺仪x,y,z：gyroscopeX,gyroscopeX,gyroscopeZ
                //加速度器x,y,z：accelerX,accelerY,accelerZ
                //屏幕状态：screenStatus（true/false）
                //天气:weather,windScale,windSpeed
                //GPS:latitude,longitude
                //标签：place,action
                //以上是数据，直接写入table

                System.out.println(weather+"  "+windScale+"  "+windSpeed+"  "+latitude+"  "+longitude);
                // 重新安排代码在10秒后再次运行
                handler.postDelayed(this, 10000);
            }
        };

        // 首次运行
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 当活动销毁时，取消定时操作，以避免内存泄漏
        handler.removeCallbacksAndMessages(null);
    }


    //天气
    public void getTheater() {
        double number = Double.parseDouble(latitude);
        String lat = (int) number + "." + String.format("%02d", (int) ((number - (int) number) * 100));
        double number1 = Double.parseDouble(longitude);
        String lon = (int) number1 + "." + String.format("%02d", (int) ((number1 - (int) number1) * 100));

        String xy=lat+","+lon;
//        Log.i("MainActivity",xy);
        QWeather.getWeatherNow(MainActivity.this, xy, Lang.ZH_HANS, Unit.METRIC,
                new QWeather.OnResultWeatherNowListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(WeatherNowBean weatherNowBean) {
                        //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                        if (Code.OK == weatherNowBean.getCode()) {
//                            WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                            weather = String.valueOf(weatherNowBean.getNow().getText());
                            windScale = String.valueOf(weatherNowBean.getNow().getWindScale());
                            windSpeed = String.valueOf(weatherNowBean.getNow().getWindSpeed());
                            //由于是在非主线程中更新文本信息，所以需要调用主线程方法
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tianqi.setText("天气" + now.getText());
//
//                                }
//                            });

                        } else {
                            //在此查看返回数据失败的原因
                            Code code = weatherNowBean.getCode();
                            Log.i(TAG, "failed code: " + code);
                        }
                    }

                });
    }

    //GPS
    public void getGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i("Permission-手动", "GPS权限未开启");
            //获取权限
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }
        // location();

        //3种获取GPS实例方法，从网络，到GPS，再到被动获取的顺序
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location==null){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location==null){
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        //输出经纬度
        if(location!=null){
            //获取经纬度
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }else {
            Log.i("Permission", "传感器为空");
            Log.i("Permission", String.valueOf(location));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Notice1(View view){
//        Log.i("MainActivity","提醒按钮1被点击");

        //权限检查与获取
        areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!areNotificationsEnabled) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
            this.startActivity(intent);
        }

        Intent intent_Main = new Intent(this, MainActivity.class);
        Intent intent_Action1 = new Intent(this,ActionActivity.class);
        Intent intent_Action2 = new Intent(this,ActionActivity.class);

        intent_Action1.setAction("打开");
        intent_Action1.addCategory("勿扰模式");
        intent_Action1.putExtra("nid",nid);
        intent_Action1.setPackage(String.valueOf(this));

        intent_Action2.setAction("关闭");
        intent_Action2.addCategory("勿扰模式");
        intent_Action2.putExtra("nid",nid);
        intent_Action2.setPackage(String.valueOf(this));

        //转到主页
        PendingIntent pending_Main=PendingIntent.getActivity(this, 0,
                intent_Main, PendingIntent.FLAG_MUTABLE);
        //覆盖前一个通知
//        PendingIntent pending_Action=PendingIntent.getActivity(this, 0,
//                intent_Action, PendingIntent.FLAG_MUTABLE);
        //不覆盖前一个通知
        PendingIntent pending_Action1=PendingIntent.getBroadcast(this, nid,
                intent_Action1, PendingIntent.FLAG_MUTABLE);
        PendingIntent pending_Action2=PendingIntent.getBroadcast(this, nid,
                intent_Action2, PendingIntent.FLAG_MUTABLE);

        //震动时长设置
        long[] vibrationPattern = {500, 500, 500, 500};

        //通知内容
        Notification notification=new NotificationCompat.Builder(this,important)
                .setSmallIcon(R.drawable.baseline_smartphone_24)
                .setContentTitle("勿扰模式")
                .setContentText("请选择开启还是关闭")
                .setVibrate(vibrationPattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pending_Main)
                .addAction(0,"去打开",pending_Action1)
                .addAction(0,"去关闭",pending_Action2)
                .setWhen(System.currentTimeMillis())
                .setGroup("myGroup")
                .build();

        //显示通知
        notificationManagerCompat.notify(nid++,notification);
    }

    public void Notice2(View view) throws Exception {
        Log.i("MainActivity","提醒按钮2被点击");
//        getAppInfo(this);
//        openApp(this);



    }

//    private void getAppInfo(Context context) throws Exception{
//        PackageManager packageManager = context.getPackageManager();
//        //获取所有安装的app
//        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
//        for(PackageInfo info : installedPackages){
//            String packageName = info.packageName;//app包名
//            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
//            String appName = (String) packageManager.getApplicationLabel(ai);//获取应用名称
//            Log.i("MainActivity",appName);
//        }
//    }
//
//    public static void openApp(Context context) {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        System.out.println("intent: " + intent);
//        context.startActivity(intent);
//    }
}