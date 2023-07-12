package com.example.habitassistant.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.habitassistant.R;
import com.example.habitassistant.utils.ChartHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

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
        radioGroup = rootView.findViewById(R.id.radioGroup);
        //显示某行为较前一天的比较
        mParam1 = "比较";
        textView.setText(mParam1);

        //请求获取数据，填入每个list
        studyEntries = new ArrayList<>();
        sportEntries = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            studyEntries.add(new BarEntry(i, 10 * i));
            sportEntries.add(new BarEntry(i, 10 * i));
        }

        ChartHelper chartHelper = new ChartHelper();
        barChart = rootView.findViewById(R.id.barChart);
        pieChart = rootView.findViewById(R.id.pieChart);
        //设置横坐标值
        barChart.getXAxis().setValueFormatter(chartHelper.getXvalue());

        pieChart.setData(chartHelper.createPieChart());
        pieChart.invalidate();
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