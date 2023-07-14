package com.example.habitassistant.fragment;

import static android.graphics.Color.rgb;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.habitassistant.R;
import com.example.habitassistant.utils.ChartHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        radioGroup = rootView.findViewById(R.id.radioGroup);
        ChartHelper chartHelper = new ChartHelper();
        barChart = rootView.findViewById(R.id.barChart);
        pieChart = rootView.findViewById(R.id.pieChart);

        //显示某行为较前一天的比较
        textView.setText("周");

        //请求获取数据，填入每个list
        studyEntries = new ArrayList<>();
        sportEntries = new ArrayList<>();
        pieEntryList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            studyEntries.add(new BarEntry(i, 10 * i));
            sportEntries.add(new BarEntry(i, 10 * i));
        }
        //饼状图数据
        pieEntryList.add(new PieEntry(30, "学习"));
        pieEntryList.add(new PieEntry(30, "睡眠"));
        pieEntryList.add(new PieEntry(30, "用餐"));
        pieEntryList.add(new PieEntry(30, "摸鱼"));
        pieEntryList.add(new PieEntry(30, "运动"));
        pieEntryList.add(new PieEntry(30, "通勤"));

        //设置横坐标值
        barChart.getXAxis().setValueFormatter(chartHelper.getWeekXvalue());

        pieChart.setData(chartHelper.createPieChart(pieEntryList));
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
        pieChart.invalidate();

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setTextSize(15);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setText("单位：小时");
        barChart.getDescription().setTextSize(14);
        barChart.getLegend().setTextSize(15);
        barChart.getLegend().setFormSize(14);
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