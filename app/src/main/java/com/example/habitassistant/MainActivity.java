package com.example.habitassistant;

import static android.content.ContentValues.TAG;
import static com.example.habitassistant.NotitionActivity.important;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.baidu.mapapi.search.geocode.GeoCoder;
import com.example.habitassistant.fragment.PersonalFragment;
import com.example.habitassistant.fragment.ScheduleFragment;
import com.example.habitassistant.fragment.StatisticsFragment;
import com.example.habitassistant.utils.MyFragmentStateAdapter;
import com.example.habitassistant.utils.ScreenStatusChecker;
import com.example.habitassistant.utils.SensorHandler;
import com.example.habitassistant.utils.TimeConverter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SensorHandler.SensorDataListener {

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

    private String info;

    private int nid = 1;
    private GeoCoder geoCoder;

    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private List<Fragment> mData;
    private StatisticsFragment statisticsFragment;
    private ScheduleFragment scheduleFragment;
    private PersonalFragment personalFragment;
    private String info_old;
    private String info_new;
    private boolean jiaoxuelou_state = true;
    private boolean tushuguan_state = true;
    private boolean xiuxi_state = true;
    private boolean tushuguanyuding_state = true;
    private boolean getin;
    private static int wurao_nid = 1;
    private static int vx_nid = 10;
    private static int music_nid = 20;
    private String place;
    private String url = "http://192.168.105.225:8000/infer/send-status";
    private String url1 = "http://192.168.105.225:8000/users/me/states";
    private String token;
    private String newState = "";
    private String oldState = "";
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 在合适的位置初始化百度地图SDK
//        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
//        SDKInitializer.initialize(getApplicationContext());

        //通知
        notificationManagerCompat = NotificationManagerCompat.from(this);
        areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

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

        mViewPager = findViewById(R.id.main_viewPager);
        mBottomNavigationView = findViewById(R.id.main_bottomNavigationView);
        // 设置适配器
        mViewPager.setAdapter(new MyFragmentStateAdapter(this, initData()));
        mViewPager.setOffscreenPageLimit(1); //设置页面缓存的个数，默认1个
        //设置底部导航栏 item 点击的监听
        mBottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        // 设置 ViewPager2 页面改变的监听
        mViewPager.registerOnPageChangeCallback(onPageChangeCallback);
        try {
            getGPS();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pemissioncheck();
        }
        location_action();
        sendInfo();
        getState();
        SharedPreferences sp = getSharedPreferences("loginSera", MODE_PRIVATE);
        token = sp.getString("token", "");
    }

    private void location_action() {

        if (!Objects.equals(info_new, info_old)) {
            //进入教学楼
            if (info_new.contains("教学楼")) {
                getin = true;
                //TODO


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            //出教学楼
            if (info_old != null && info_old.contains("教学楼")) {
                getin = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            //进入
            if (info_new.contains("图书馆")) {
                getin = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            //出
            if (info_old != null && info_old.contains("图书馆")) {
                getin = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    n_wurao();
                }
            }
            info_old = info_new;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pemissioncheck() {
        //勿扰模式权限
        boolean areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!areNotificationsEnabled) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
            this.startActivity(intent);
        }

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

    @Override
    public void onClick(View v) {

    }


    private void sendInfo() {
        // 这个 Runnable 对象定义了每10秒执行一次的代码
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        screenStatus = screenStatusChecker.isScreenOn();
                        try {
                            OkHttpClient client = new OkHttpClient();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("gyroscopeX", gyroscopeX);
                            jsonObject.put("gyroscopeY", gyroscopeY);
                            jsonObject.put("gyroscopeZ", gyroscopeZ);
                            jsonObject.put("accelerateX", accelerX);
                            jsonObject.put("accelerateY", accelerY);
                            jsonObject.put("accelerateZ", accelerZ);
                            jsonObject.put("screenStatus", screenStatus);
                            jsonObject.put("latitude", latitude);
                            jsonObject.put("longitude", longitude);
                            jsonObject.put("time", TimeConverter.getTime());
                            jsonObject.put("date", TimeConverter.getDate());
                            jsonObject.put("place", place);
                            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
                            Request request = new Request.Builder()
                                    .url(url)
                                    .header("Authorization", "Bearer " + token)
                                    .post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (response.code() == 200) {
                                //保存token以及token_type
                                String data = response.body().string();
                                Log.i("发送数据成功", data);
                            } else {
                                Log.i("发送数据失败", "失败");
                            }
                            getGPS();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        getTheater();
                    }
                }).start();
                //陀螺仪x,y,z：gyroscopeX,gyroscopeX,gyroscopeZ
                //加速度器x,y,z：accelerX,accelerY,accelerZ
                //屏幕状态：screenStatus（true/false）
                //天气:weather,windScale,windSpeed
                //GPS:latitude,longitude
                //标签：place,action
                //以上是数据，直接写入table
                // 重新安排代码在10秒后再次运行
                handler.postDelayed(this, 10000);
            }
        };

        // 首次运行
        handler.post(runnable);
    }

    private void getState() {
        // 这个 Runnable 对象定义了每10秒执行一次的代码
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url1)
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (response.code() == 200) {
                                JSONArray jsonArray = new JSONArray(response.body().string());
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                oldState = newState;
                                newState = jsonObject.getString("state");
                                if (newState.equals("学习") && !oldState.equals(newState)) {
                                    //打开勿扰
                                    open_notification("打开", ":勿扰模式", "检测到您进入学习状态", "已经为您打开勿扰模式");
                                } else if (newState.equals("运动") && !oldState.equals(newState)) {
                                    //打开音乐
                                    open_notification("打开音乐", ":应用管理", "检测到您正在运动", "可以听点音乐");
                                } else if (newState.equals("睡觉") && !oldState.equals(newState)) {
                                    //打开勿扰
                                    open_notification("打开", ":勿扰模式", "检测到您进入睡眠", "已经为您打开勿扰模式");
                                }
                                //Log.i("获取最近状态成功",response.body().string());
                            } else {
                                Log.i("获取最近状态成功", "失败");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }).start();
                //陀螺仪x,y,z：gyroscopeX,gyroscopeX,gyroscopeZ
                //加速度器x,y,z：accelerX,accelerY,accelerZ
                //屏幕状态：screenStatus（true/false）
                //天气:weather,windScale,windSpeed
                //GPS:latitude,longitude
                //标签：place,action
                //以上是数据，直接写入table
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

        String xy = lat + "," + lon;
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
    public void getGPS() throws IOException {
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
        }
        // location();

        //3种获取GPS实例方法，从网络，到GPS，再到被动获取的顺序
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }

        //输出经纬度
        if (location != null) {
            //获取经纬度
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            double la = location.getLatitude();
            double lo = location.getLongitude();

            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                // 获取经纬度对于的位置
                // getFromLocation(纬度, 经度, 最多获取的位置数量)
                List<Address> addresses = geocoder.getFromLocation(la, lo, 1);
                // 得到第一个经纬度位置解析信息
                Address address = addresses.get(0);
                // 获取到详细的当前位置
                // Address里面还有很多方法你们可以自行实现去尝试。比如具体省的名称、市的名称...
                // 获取省市县(区)
                String info = address.getAddressLine(1) + // 获取省市县(区)
                        address.getAddressLine(2);  // 获取镇号(地址名称)
                System.out.println(info);
                info_new = info;
                location_send();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Log.i("Permission", "传感器为空");
            Log.i("Permission", String.valueOf(location));
        }

    }

    ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            int itemID = R.id.statistics;
            switch (position) {
                case 0:
                    itemID = R.id.statistics;
                    Log.d("HL", "1");
                    break;
                case 1:
                    itemID = R.id.schedule;
                    Log.d("HL", "2");
                    break;
                case 2:
                    itemID = R.id.personal;
                    Log.d("HL", "3");
                    break;
                default:
                    break;
            }
            //TODO 当Fragment滑动改变时，底部的Tab也跟着改变
            mBottomNavigationView.setSelectedItemId(itemID);
        }
    };

    NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // 点击Tab, 切换对应的 Fragment
            if (item.getItemId() == R.id.statistics) {
                mViewPager.setCurrentItem(0, true);
                return true;
            } else if (item.getItemId() == R.id.schedule) {
                mViewPager.setCurrentItem(1, true);
                return true;
            } else if (item.getItemId() == R.id.personal) {
                mViewPager.setCurrentItem(2, true);
                return true;
            } else {
                return false;
            }
        }
    };

    private List<Fragment> initData() {
        mData = new ArrayList<>();
        statisticsFragment = new StatisticsFragment();
        scheduleFragment = new ScheduleFragment();
        personalFragment = new PersonalFragment();
        mData.add(statisticsFragment);
        mData.add(scheduleFragment);
        mData.add(personalFragment);
        return mData;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void n_wurao() {

        if (jiaoxuelou_state) {
            Intent intent_Main = new Intent(this, MainActivity.class);
            Intent intent_Action1 = new Intent(this, ActionActivity.class);
            Intent intent_Action2 = new Intent(this, ActionActivity.class);

            intent_Action1.setAction("打开");
            intent_Action1.addCategory("勿扰模式");

            if (getin == true) {
                //开启勿扰
                sendBroadcast(intent_Action1);
            }

            intent_Action1.putExtra("nid", wurao_nid);
            intent_Action1.setPackage(String.valueOf(this));

            intent_Action2.setAction("关闭");
            intent_Action2.addCategory("勿扰模式");

            if (getin != true) {
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

            if (getin == true) {
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

            } else if (getin == false) {
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
        } else if (!jiaoxuelou_state) {
            Log.i("MainActivity", "已关闭该通知");
        }
    }

    public void n_openvx() {
        //        Log.i("MainActivity","提醒按钮1被点击");

        Intent intent_Main = new Intent(this, MainActivity.class);
        Intent intent_Action1 = new Intent(this, ActionActivity.class);

        intent_Action1.setAction("打开微信");
        intent_Action1.addCategory("应用管理");
        intent_Action1.putExtra("nid", vx_nid);
        intent_Action1.setPackage(String.valueOf(this));

        //转到主页
        PendingIntent pending_Main = PendingIntent.getActivity(this, 0,
                intent_Main, PendingIntent.FLAG_MUTABLE);
        //覆盖前一个通知
        PendingIntent pending_Action1 = PendingIntent.getBroadcast(this, vx_nid,
                intent_Action1, PendingIntent.FLAG_MUTABLE);


        //震动时长设置
        long[] vibrationPattern = {500, 500, 500, 500};

        //通知内容
        Notification notification = new NotificationCompat.Builder(this, important)
                .setSmallIcon(R.drawable.baseline_smartphone_24)
                .setContentTitle("定时提醒")
                .setContentText("已经到了图书馆预订位置时间，请打开微信去预订座位")
                .setVibrate(vibrationPattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pending_Main)
                .addAction(0, "去打开", pending_Action1)
                .setWhen(System.currentTimeMillis())
                .build();

        //显示通知
        notificationManagerCompat.notify(vx_nid, notification);
    }

    public void n_openmusic() {
        //        Log.i("MainActivity","提醒按钮1被点击");

        Intent intent_Main = new Intent(this, MainActivity.class);
        Intent intent_Action1 = new Intent(this, ActionActivity.class);

        intent_Action1.setAction("打开音乐");
        intent_Action1.addCategory("应用管理");
        intent_Action1.putExtra("nid", music_nid);
        intent_Action1.setPackage(String.valueOf(this));

        //转到主页
        PendingIntent pending_Main = PendingIntent.getActivity(this, 0,
                intent_Main, PendingIntent.FLAG_MUTABLE);
        PendingIntent pending_Action1 = PendingIntent.getBroadcast(this, music_nid,
                intent_Action1, PendingIntent.FLAG_MUTABLE);

        //震动时长设置
        long[] vibrationPattern = {500, 500, 500, 500};

        //通知内容
        Notification notification = new NotificationCompat.Builder(this, important)
                .setSmallIcon(R.drawable.baseline_smartphone_24)
                .setContentTitle("休息提醒")
                .setContentText("你已经很辛苦了，请听点音乐，放松一下吧")
                .setVibrate(vibrationPattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pending_Main)
                .addAction(0, "开摆", pending_Action1)
                .setWhen(System.currentTimeMillis())
                .build();

        //显示通知
        notificationManagerCompat.notify(music_nid, notification);
    }

    private void location_send() {
        if (info_new.contains("教学楼") || info_new.contains("思源楼") || info_new.contains("办公楼")) {
            place = "教室";
        } else if (info_new.contains("公寓")) {
            place = "宿舍";
        } else if (info_new.contains("食堂") || info_new.contains("餐厅")) {
            place = "食堂";
        } else {
            place = "其他";
        }
    }

    private void open_notification(String action, String category, String title, String text) {
        Intent intent_Main = new Intent(this, MainActivity.class);
        Intent intent_Action1 = new Intent(this, ActionActivity.class);
        Intent intent_Action2 = new Intent(this, ActionActivity.class);

        intent_Action1.setAction(action);
        intent_Action1.addCategory(category);

        if (Objects.equals(action, "打开")) {
            //开启勿扰
            sendBroadcast(intent_Action1);

            intent_Action2.setAction("关闭");
            intent_Action2.addCategory(category);
            intent_Action2.putExtra("nid", wurao_nid);
            intent_Action2.setPackage(String.valueOf(this));

            //转到主页
            PendingIntent pending_Main = PendingIntent.getActivity(this, 0,
                    intent_Main, PendingIntent.FLAG_MUTABLE);
            //转到通知
            PendingIntent pending_Action2 = PendingIntent.getBroadcast(this, wurao_nid,
                    intent_Action2, PendingIntent.FLAG_MUTABLE);

            //震动时长设置
            long[] vibrationPattern = {500, 500, 500, 500};

            //通知内容
            Notification notification = new NotificationCompat.Builder(this, important)
                    .setSmallIcon(R.drawable.baseline_smartphone_24)
                    .setContentTitle(title)
                    .setContentText(text)
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
        } else if (Objects.equals(action, "打开音乐")) {

            intent_Action1.setAction(action);
            intent_Action1.addCategory(category);
            intent_Action1.putExtra("nid", music_nid);
            intent_Action1.setPackage(String.valueOf(this));

            //转到主页
            PendingIntent pending_Main = PendingIntent.getActivity(this, 0,
                    intent_Main, PendingIntent.FLAG_MUTABLE);
            PendingIntent pending_Action1 = PendingIntent.getBroadcast(this, music_nid,
                    intent_Action1, PendingIntent.FLAG_MUTABLE);

            //震动时长设置
            long[] vibrationPattern = {500, 500, 500, 500};

            //通知内容
            Notification notification = new NotificationCompat.Builder(this, important)
                    .setSmallIcon(R.drawable.baseline_smartphone_24)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setVibrate(vibrationPattern)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(pending_Main)
                    .addAction(0, "知道了", pending_Action1)
                    .setWhen(System.currentTimeMillis())
                    .build();

            //显示通知
            notificationManagerCompat.notify(music_nid, notification);

        } else if (Objects.equals(action, "打开微信")) {
            intent_Action1.setAction(action);
            intent_Action1.addCategory(category);
            intent_Action1.putExtra("nid", vx_nid);
            intent_Action1.setPackage(String.valueOf(this));

            //转到主页
            PendingIntent pending_Main = PendingIntent.getActivity(this, 0,
                    intent_Main, PendingIntent.FLAG_MUTABLE);
            //覆盖前一个通知
            PendingIntent pending_Action1 = PendingIntent.getBroadcast(this, vx_nid,
                    intent_Action1, PendingIntent.FLAG_MUTABLE);


            //震动时长设置
            long[] vibrationPattern = {500, 500, 500, 500};

            //通知内容
            Notification notification = new NotificationCompat.Builder(this, important)
                    .setSmallIcon(R.drawable.baseline_smartphone_24)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setVibrate(vibrationPattern)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(pending_Main)
                    .addAction(0, "知道了", pending_Action1)
                    .setWhen(System.currentTimeMillis())
                    .build();

            //显示通知
            notificationManagerCompat.notify(vx_nid, notification);

        } else {
            intent_Action1.setAction(action);
            intent_Action1.addCategory(category);
            intent_Action1.putExtra("nid", vx_nid);
            intent_Action1.setPackage(String.valueOf(this));

            //转到主页
            PendingIntent pending_Main = PendingIntent.getActivity(this, 0,
                    intent_Main, PendingIntent.FLAG_MUTABLE);
            //覆盖前一个通知
            PendingIntent pending_Action1 = PendingIntent.getBroadcast(this, vx_nid,
                    intent_Action1, PendingIntent.FLAG_MUTABLE);


            //震动时长设置
            long[] vibrationPattern = {500, 500, 500, 500};

            //通知内容
            Notification notification = new NotificationCompat.Builder(this, important)
                    .setSmallIcon(R.drawable.baseline_smartphone_24)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setVibrate(vibrationPattern)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(pending_Main)
                    .addAction(0, "知道了", pending_Action1)
                    .setWhen(System.currentTimeMillis())
                    .build();

            //显示通知
            notificationManagerCompat.notify(vx_nid, notification);
        }
    }
    public boolean getJiaoxuelou_state() {
        return jiaoxuelou_state;
    }

    public void setJiaoxuelou_state(boolean jiaoxuelou_state) {
        this.jiaoxuelou_state = jiaoxuelou_state;
    }

    public boolean getTushuguan_state() {
        return tushuguan_state;
    }

    public void setTushuguan_state(boolean tushuguan_state) {
        this.tushuguan_state = tushuguan_state;
    }

    public boolean getXiuxi_state() {
        return xiuxi_state;
    }

    public void setXiuxi_state(boolean xiuxi_state) {
        this.xiuxi_state = xiuxi_state;
    }

    public boolean getTushuguanyuding_state() {
        return tushuguanyuding_state;
    }

    public void setTushuguanyuding_state(boolean tushuguanyuding_state) {
        this.tushuguanyuding_state = tushuguanyuding_state;
    }
}