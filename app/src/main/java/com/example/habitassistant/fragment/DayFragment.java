package com.example.habitassistant.fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.rgb;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.habitassistant.R;
import com.example.habitassistant.utils.ChartHelper;
import com.example.habitassistant.utils.TimeConverter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {
    private TextView textView;
    private TextView recommend;
    private String txtRecommend;
    //绘制图表
    private ChartHelper chartHelper;
    private PieChart pieChart;
    private LineChart lineChart;
    List<PieEntry> pieEntryList;
    List<ILineDataSet> dataSets;
    //日期选择
    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    private Button btnDatePicker;
    private String url = "http://192.168.105.225:8000/users/me/states";
    private String url1 = "http://192.168.105.225:8000/users/me/statistics";
    private String url2 = "http://192.168.105.225:8000/users/me/states/recommendation";
    SharedPreferences sp;
    String token;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance() {
        DayFragment fragment = new DayFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);
        textView = rootView.findViewById(R.id.compare);
        recommend = rootView.findViewById(R.id.recommend);
        textView.setText("当前状态：学习");
        recommend.setText("来自ChatGPT的建议！");
        chartHelper = new ChartHelper();
        //标题栏
        textView.setTextSize(30);
        //日期选择
        btnDatePicker = rootView.findViewById(R.id.btnDatePicker);
        btnDatePicker.setBackgroundColor(WHITE);
        btnDatePicker.setTextColor(BLACK);
        btnDatePicker.setText(year + "年" + (month + 1) + "月" + day + "日" + "▼");
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(rootView);
            }
        });
        sp = getActivity().getSharedPreferences("loginSera", MODE_PRIVATE);
        token = sp.getString("token", "");

        //请求获取数据，填入每个list
        //绘制当天
        drawLineChart(rootView, day);
        drawPieChart(rootView, day);
        //获取建议
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url2)
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
                                txtRecommend = responseData;
                                // 使用Activity的runOnUiThread方法切回主线程
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里更新Fragment的UI
                                        //绘制饼状图
                                        recommend.setText(txtRecommend);
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

        return rootView;
    }

    private void showDatePicker(View rootView) {
        // 获取当前日期
//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
        // 创建日期选择器对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int years, int months, int dayOfMonth) {
                // 处理选定的日期
                // 在这里可以将选定的日期传递给Activity或进行其他处理
                // year: 年份
                // month: 月份（注意：0表示1月，11表示12月）
                // dayOfMonth: 日期
                Log.i("选择的日期", String.valueOf(dayOfMonth));
                drawLineChart(rootView, dayOfMonth);
                drawPieChart(rootView, dayOfMonth);
                btnDatePicker.setText(years + "年" + (months + 1) + "月" + dayOfMonth + "日" + "▼");
                day = dayOfMonth;
            }
        }, year, month, day);

        // 显示日期选择器对话框
        datePickerDialog.show();
    }

    private void drawLineChart(View rootView, int chosenDay) {
        dataSets = new ArrayList<>();
        //设置折线图数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    url = "http://192.168.105.225:8000/users/me/states?start_date=2023-7-" + chosenDay + "&end_date=2023-7-" + chosenDay;
                    Log.i("url",url);
                    //url = url + "?start_date=2023-7-10&end_date=2023-7-10";
                    Request request = new Request.Builder()
                            .url(url)
                            .header("Authorization", "Bearer " + token)
                            .build();
                    Log.i("token", token);
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                lineChart = rootView.findViewById(R.id.lineChart);
                                String statue = "";
                                final String responseData = response.body().string();
                                float start_time = 0;
                                float end_time = 0;
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
                                        statue = jsonObject.getString("state");
                                        start_time = TimeConverter.convertTime(jsonObject.getString("start_time"));
                                        end_time = TimeConverter.convertTime(jsonObject.getString("end_time"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (statue.equals("学习")) {
                                        List<Entry> entries = new ArrayList<>();
                                        //添加数据
                                        entries.add(new Entry(start_time, 15));
                                        entries.add(new Entry(end_time, 15));
                                        LineDataSet lineDataSet = new LineDataSet(entries, "");
                                        lineDataSet.setColor(rgb(255, 154, 162));
                                        lineDataSet.setLineWidth(10);
                                        lineDataSet.setDrawValues(false);
                                        dataSets.add(lineDataSet);
                                    } else if (statue.equals("睡觉")) {
                                        List<Entry> entries = new ArrayList<>();
                                        //添加数据
                                        entries.add(new Entry(start_time, 15));
                                        entries.add(new Entry(end_time, 15));
                                        LineDataSet lineDataSet = new LineDataSet(entries, "");
                                        lineDataSet.setColor(rgb(255, 180, 71));
                                        lineDataSet.setLineWidth(10);
                                        lineDataSet.setDrawValues(false);
                                        dataSets.add(lineDataSet);
                                    } else if (statue.equals("吃饭")) {
                                        List<Entry> entries = new ArrayList<>();
                                        //添加数据
                                        entries.add(new Entry(start_time, 15));
                                        entries.add(new Entry(end_time, 15));
                                        LineDataSet lineDataSet = new LineDataSet(entries, "");
                                        lineDataSet.setColor(rgb(254, 228, 64));
                                        lineDataSet.setLineWidth(10);
                                        lineDataSet.setDrawValues(false);
                                        dataSets.add(lineDataSet);
                                    } else if (statue.equals("摸鱼")) {
                                        List<Entry> entries = new ArrayList<>();
                                        //添加数据
                                        entries.add(new Entry(start_time, 15));
                                        entries.add(new Entry(end_time, 15));
                                        LineDataSet lineDataSet = new LineDataSet(entries, "");
                                        lineDataSet.setColor(rgb(189, 232, 118));
                                        lineDataSet.setLineWidth(10);
                                        lineDataSet.setDrawValues(false);
                                        dataSets.add(lineDataSet);
                                    } else if (statue.equals("运动")) {
                                        List<Entry> entries = new ArrayList<>();
                                        //添加数据
                                        entries.add(new Entry(start_time, 15));
                                        entries.add(new Entry(end_time, 15));
                                        LineDataSet lineDataSet = new LineDataSet(entries, "");
                                        lineDataSet.setColor(rgb(160, 221, 255));
                                        lineDataSet.setLineWidth(10);
                                        lineDataSet.setDrawValues(false);
                                        dataSets.add(lineDataSet);
                                    } else {
                                        List<Entry> entries = new ArrayList<>();
                                        //添加数据
                                        entries.add(new Entry(start_time, 15));
                                        entries.add(new Entry(end_time, 15));
                                        LineDataSet lineDataSet = new LineDataSet(entries, "");
                                        lineDataSet.setColor(rgb(157, 132, 183));
                                        lineDataSet.setLineWidth(10);
                                        lineDataSet.setDrawValues(false);
                                        dataSets.add(lineDataSet);
                                    }
                                }
                                // 使用Activity的runOnUiThread方法切回主线程
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里更新Fragment的UI
                                        //绘制折线图
                                        List<LegendEntry> legendEntries = new ArrayList<>();
                                        legendEntries.add(new LegendEntry(
                                                "学习", Legend.LegendForm.SQUARE, 14, 9, null, rgb(255, 154, 162)));
                                        legendEntries.add(new LegendEntry(
                                                "睡眠", Legend.LegendForm.SQUARE, 14, 9, null, rgb(255, 180, 71)));
                                        legendEntries.add(new LegendEntry(
                                                "用餐", Legend.LegendForm.SQUARE, 14, 9, null, rgb(254, 228, 64)));
                                        legendEntries.add(new LegendEntry(
                                                "摸鱼", Legend.LegendForm.SQUARE, 14, 9, null, rgb(189, 232, 118)));
                                        legendEntries.add(new LegendEntry(
                                                "运动", Legend.LegendForm.SQUARE, 14, 9, null, rgb(160, 221, 255)));
                                        legendEntries.add(new LegendEntry(
                                                "通勤", Legend.LegendForm.SQUARE, 14, 9, null, rgb(157, 132, 183)));
                                        lineChart.getLegend().setCustom(legendEntries);
                                        lineChart.getLegend().setFormSize(15);
                                        lineChart.getLegend().setTextSize(14);
                                        //lineChart.getXAxis().setValueFormatter(chartHelper.getDayXvalue());
                                        lineChart.setData(chartHelper.createLineChart(dataSets));
                                        lineChart.getXAxis().setDrawGridLines(false);
                                        lineChart.getAxisLeft().setDrawGridLines(false);
                                        lineChart.getAxisLeft().setEnabled(false);
                                        lineChart.getAxisRight().setEnabled(false);
                                        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                        lineChart.getDescription().setText("今日行程");
                                        lineChart.getDescription().setPosition(220, 50);
                                        lineChart.getDescription().setTextSize(20);
                                        lineChart.invalidate();
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("警告", String.valueOf(e));
                }
            }
        }).start();
    }

    private void drawPieChart(View rootView, int chosenDay) {
        pieEntryList = new ArrayList<>();
        //饼状图数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    url1 = "http://192.168.105.225:8000/users/me/statistics?start_date=2023-7-" + chosenDay + "&end_date=2023-7-" + chosenDay;
                    //url1 = url1 + "?start_date=2023-7-10&end_date=2023-7-10";
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
                                        pieEntryList.add(new PieEntry(TimeConverter.getHour(jsonObject.getInt("total_time")), jsonObject.getString("state")));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                // 使用Activity的runOnUiThread方法切回主线程
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里更新Fragment的UI
                                        //绘制饼状图
                                        pieChart.setData(chartHelper.createPieChart(pieEntryList));
                                        pieChart.setBackgroundColor(rgb(232, 234, 237));
                                        pieChart.getDescription().setText("单位：小时");
                                        pieChart.getDescription().setTextSize(15);
                                        //实体扇形的空心圆的半径   设置成0时就是一个圆 而不是一个环
                                        pieChart.setHoleRadius(30);
                                        //中间半透明白色圆的半径    设置成0时就是隐藏
                                        pieChart.setTransparentCircleRadius(0);
                                        pieChart.setCenterText("今日统计");
                                        pieChart.setCenterTextSize(15);
                                        pieChart.getLegend().setFormSize(15);
                                        pieChart.getLegend().setTextSize(14);
                                        pieChart.setEntryLabelColor(Color.BLACK);
                                        pieChart.setEntryLabelTextSize(15);
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
    }
}