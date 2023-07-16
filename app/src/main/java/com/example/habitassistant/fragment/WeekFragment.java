package com.example.habitassistant.fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.rgb;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.habitassistant.R;
import com.example.habitassistant.utils.ChartHelper;
import com.example.habitassistant.utils.TimeConverter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekFragment extends Fragment {
    private TextView textView;
    private RadioGroup radioGroup;
    //绘制图表
    private BarChart barChart;
    private PieChart pieChart;
    List<BarEntry> studyEntries;
    List<BarEntry> sportEntries;
    List<BarEntry> transportEntries;
    List<BarEntry> sleepEntries;
    List<BarEntry> eatingEntries;
    List<BarEntry> phoneEntries;
    List<PieEntry> pieEntryList;

    private String url = "http://192.168.105.225:8000/users/me/states";
    private String url1 = "http://192.168.105.225:8000/users/me/statistics";

    public WeekFragment() {
        // Required empty public constructor
    }

    public static WeekFragment newInstance() {
        WeekFragment fragment = new WeekFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_week, container, false);
        textView = rootView.findViewById(R.id.compare);
        textView.setTextSize(30);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        ChartHelper chartHelper = new ChartHelper();
        barChart = rootView.findViewById(R.id.barChart);
        pieChart = rootView.findViewById(R.id.pieChart);

        SharedPreferences sp = getActivity().getSharedPreferences("loginSera", MODE_PRIVATE);
        String token = sp.getString("token", "");
        //显示某行为较前一天的比较
        textView.setText("周统计");

        //请求获取数据，填入每个list
        pieEntryList = new ArrayList<>();
        studyEntries = new ArrayList<>();
        sportEntries = new ArrayList<>();
        transportEntries = new ArrayList<>();
        sleepEntries = new ArrayList<>();
        phoneEntries = new ArrayList<>();
        eatingEntries = new ArrayList<>();
        int[] day = new int[]{0,0,0,0,0,0};

        //请求获取柱状图数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //url = url + "?start_date=2023-7-"+day+"&end_date=2023-7-"+day;
                    url1 = url1 + "?start_date=2023-7-10&end_date=2023-7-16";
                    Request request = new Request.Builder()
                            .url(url1)
                            .header("Authorization", "Bearer " + token)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                barChart = rootView.findViewById(R.id.barChart);
                                final String responseData = response.body().string();
                                JSONArray jsonArray = null;
                                int minutes = 0;
                                String state = "";
                                try {
                                    jsonArray = new JSONArray(responseData);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        state = jsonObject.getString("state");
                                        minutes = jsonObject.getInt("total_time");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (state.equals("学习")) {
                                        studyEntries.add(new BarEntry(day[0], TimeConverter.getHour(minutes)));
                                        day[0]++;
                                    } else if (state.equals("运动")) {
                                        sportEntries.add(new BarEntry(day[1], TimeConverter.getHour(minutes)));
                                        day[1]++;
                                    } else if (state.equals("通勤")) {
                                        transportEntries.add(new BarEntry(day[2], TimeConverter.getHour(minutes)));
                                        day[2]++;
                                    } else if (state.equals("睡觉")) {
                                        sleepEntries.add(new BarEntry(day[3], TimeConverter.getHour(minutes)));
                                        day[3]++;
                                    } else if (state.equals("吃饭")) {
                                        eatingEntries.add(new BarEntry(day[4], TimeConverter.getHour(minutes)));
                                        day[4]++;
                                    } else {
                                        phoneEntries.add(new BarEntry(day[5], TimeConverter.getHour(minutes)));
                                        day[5]++;
                                    }
                                }
                                // 使用Activity的runOnUiThread方法切回主线程
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里更新Fragment的UI
                                        //设
                                        //
                                        //
                                        //
                                        //
                                        // 置横坐标值
                                        barChart.getXAxis().setValueFormatter(chartHelper.getWeekXvalue());
                                        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                        barChart.getXAxis().setDrawGridLines(false);
                                        barChart.getXAxis().setTextSize(15);
                                        barChart.getAxisRight().setDrawGridLines(false);
                                        barChart.getAxisRight().setEnabled(false);
                                        barChart.getDescription().setText("单位：小时");
                                        barChart.getDescription().setPosition(1000,50);
                                        barChart.getDescription().setTextSize(14);
                                        barChart.getLegend().setTextSize(15);
                                        barChart.getLegend().setFormSize(14);
                                        barChart.setData(chartHelper.createBarChart(studyEntries, "学习"));
                                        barChart.invalidate();
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //请求获取饼状图数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //url = url + "?start_date=2023-7-"+day+"&end_date=2023-7-"+day;
                    url1 = url1 + "?start_date=2023-7-10&end_date=2023-7-10";
                    Request request = new Request.Builder()
                            .url(url1)
                            .header("Authorization", "Bearer " + token)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                pieChart = rootView.findViewById(R.id.pieChart);
                                final String responseData = response.body().string();
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(responseData);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        pieEntryList.add(new PieEntry(TimeConverter.getHour(jsonObject.getInt("total_time")),jsonObject.getString("state")));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                // 使用Activity的runOnUiThread方法切回主线程
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里更新Fragment的UI
                                        pieChart.setBackgroundColor(rgb(232, 234, 237));
                                        pieChart.getDescription().setEnabled(false);
                                        //实体扇形的空心圆的半径   设置成0时就是一个圆 而不是一个环
                                        pieChart.setHoleRadius(30);
                                        //中间半透明白色圆的半径    设置成0时就是隐藏
                                        pieChart.setTransparentCircleRadius(0);
                                        pieChart.setCenterText("本周统计");
                                        pieChart.setCenterTextSize(15);
                                        pieChart.getLegend().setFormSize(15);
                                        pieChart.getLegend().setTextSize(14);
                                        pieChart.setEntryLabelColor(Color.BLACK);
                                        pieChart.setEntryLabelTextSize(15);
                                        pieChart.setData(chartHelper.createPieChart(pieEntryList));
                                        pieChart.invalidate();
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //单选响应
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = rootView.findViewById(checkedId);
                if (Objects.equals(radioButton.getText(), "学习")) {
                    barChart.setData(chartHelper.createBarChart(studyEntries, "学习"));
                    barChart.invalidate();
                } else if (Objects.equals(radioButton.getText(), "运动")) {
                    barChart.setData(chartHelper.createBarChart(sportEntries, "运动"));
                    barChart.invalidate();
                } else if (Objects.equals(radioButton.getText(), "通勤")) {
                    barChart.setData(chartHelper.createBarChart(transportEntries, "通勤"));
                    barChart.invalidate();
                } else if (Objects.equals(radioButton.getText(), "睡眠")) {
                    barChart.setData(chartHelper.createBarChart(sleepEntries, "睡眠"));
                    barChart.invalidate();
                } else if (Objects.equals(radioButton.getText(), "用餐")) {
                    barChart.setData(chartHelper.createBarChart(eatingEntries, "用餐"));
                    barChart.invalidate();
                } else {
                    barChart.setData(chartHelper.createBarChart(phoneEntries, "摸鱼"));
                    barChart.invalidate();
                }
            }
        });
        return rootView;
    }

}