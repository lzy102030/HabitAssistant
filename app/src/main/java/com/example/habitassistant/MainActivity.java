package com.example.habitassistant;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.habitassistant.fragment.PersonalFragment;
import com.example.habitassistant.fragment.ScheduleFragment;
import com.example.habitassistant.fragment.StatisticsFragment;
import com.example.habitassistant.utils.MyFragmentStateAdapter;
import com.example.habitassistant.utils.ScreenStatusChecker;
import com.example.habitassistant.utils.SensorHandler;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
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

    private String weather;
    private String windScale;
    private String windSpeed;
    private String latitude;
    private String longitude;
    private LocationManager locationManager;
    //绘制图表
    private LineChart lineChart;
    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private List<Fragment> mData;
    private StatisticsFragment statisticsFragment;
    private ScheduleFragment scheduleFragment;
    private PersonalFragment personalFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        //绘制图表
//        lineChart = findViewById(R.id.line_chart);
//        ChartHelper chartHelper = new ChartHelper();
//        lineChart.setData(chartHelper.createLineChart());
//        lineChart.invalidate();

                mViewPager = findViewById(R.id.main_viewPager);
        mBottomNavigationView = findViewById(R.id.main_bottomNavigationView);
        // 设置适配器
        mViewPager.setAdapter(new MyFragmentStateAdapter(this, initData()));
        mViewPager.setOffscreenPageLimit(1); //设置页面缓存的个数，默认1个
        //设置底部导航栏 item 点击的监听
        mBottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        // 设置 ViewPager2 页面改变的监听
        mViewPager.registerOnPageChangeCallback(onPageChangeCallback);
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
        String lon = (int) number + "." + String.format("%02d", (int) ((number - (int) number) * 100));

        String xy=lat+","+lon;
        Log.i("MainActivity",xy);
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
                            WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
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
            if(item.getItemId()==R.id.statistics){
                mViewPager.setCurrentItem(0, true);
                return true;
            } else if (item.getItemId()==R.id.schedule) {
                mViewPager.setCurrentItem(1, true);
                return true;
            } else if (item.getItemId()==R.id.personal) {
                mViewPager.setCurrentItem(2, true);
                return true;
            }else {
                return false;
            }

//            switch (item.getItemId()) {
//                case R.id.statistics:
//                    //TODO 当点击 Tab 时，ViewPager 也切换到对应的 Fragment
//                    mViewPager.setCurrentItem(0, true);
//                    return true;
//                case R.id.schedule:
//                    mViewPager.setCurrentItem(1, true);
//                    return true;
//                case R.id.personal:
//                    mViewPager.setCurrentItem(2, true);
//                    return true;
//            }

        }
    };
    private List<Fragment> initData(){
        mData = new ArrayList<>();
        statisticsFragment = new StatisticsFragment();
        scheduleFragment = new ScheduleFragment();
        personalFragment = new PersonalFragment();
        mData.add(statisticsFragment);
        mData.add(scheduleFragment);
        mData.add(personalFragment);
        return mData;
    }
}