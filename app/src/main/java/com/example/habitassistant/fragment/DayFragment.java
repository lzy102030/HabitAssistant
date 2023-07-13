package com.example.habitassistant.fragment;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.rgb;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.habitassistant.R;
import com.example.habitassistant.utils.ChartHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {
    private TextView textView;
    //绘制图表
    private ChartHelper chartHelper;
    private PieChart pieChart;
    private LineChart lineChart;
    List<PieEntry> pieEntryList;
    List<Entry> studyEntries;
    List<Entry> sleepEntries;
    List<Entry> eatingEntries;
    List<ILineDataSet> dataSets;
    //日期选择
    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    private Button btnDatePicker;
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
        pieChart = rootView.findViewById(R.id.pieChart);
        lineChart = rootView.findViewById(R.id.lineChart);
        chartHelper = new ChartHelper();
        //标题栏
        textView.setTextSize(45);
        //日期选择
        btnDatePicker = rootView.findViewById(R.id.btnDatePicker);
        btnDatePicker.setBackgroundColor(WHITE);
        btnDatePicker.setTextColor(BLACK);
        btnDatePicker.setText(year+"年"+month+"月"+day+"日"+"▼");
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


        //请求获取数据，填入每个list
        dataSets = new ArrayList<>();
        studyEntries = new ArrayList<>();
        sleepEntries = new ArrayList<>();
        eatingEntries = new ArrayList<>();
        pieEntryList = new ArrayList<>();

        //设置折线图数据
        studyEntries.add(new Entry(0.0f, 15));
        studyEntries.add(new Entry(2.3f, 15));
        sleepEntries.add(new Entry(2.3f, 15));
        sleepEntries.add(new Entry(4.4f, 15));
        eatingEntries.add(new Entry(4.4f, 15));
        eatingEntries.add(new Entry(8.3f, 15));
        LineDataSet studySet = new LineDataSet(studyEntries, "学习");
        studySet.setColor(Color.RED);
        studySet.setLineWidth(10);
        LineDataSet sleepSet = new LineDataSet(sleepEntries, "睡觉");
        sleepSet.setColor(Color.BLUE);
        sleepSet.setLineWidth(10);
        LineDataSet eatingSet = new LineDataSet(eatingEntries,"用餐");
        eatingSet.setColor(Color.BLACK);
        eatingSet.setLineWidth(10);
        dataSets.add(studySet);
        dataSets.add(sleepSet);
        dataSets.add(eatingSet);

        //饼状图数据
        pieEntryList.add(new PieEntry(23, "学习"));
        pieEntryList.add(new PieEntry(45, "睡眠"));
        pieEntryList.add(new PieEntry(31, "用餐"));
        pieEntryList.add(new PieEntry(67, "摸鱼"));
        pieEntryList.add(new PieEntry(28, "运动"));
        pieEntryList.add(new PieEntry(16, "通勤"));

        //绘制饼状图
        pieChart.setData(chartHelper.createPieChart(pieEntryList));
        pieChart.setBackgroundColor(rgb(232, 234, 237));
        pieChart.getDescription().setEnabled(false);
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
        //绘制折线图
        lineChart.getXAxis().setValueFormatter(chartHelper.getDayXvalue());
        lineChart.setData(chartHelper.createLineChart(dataSets));
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getDescription().setText("今日行程");
        lineChart.getDescription().setPosition(220,50);
        lineChart.getDescription().setTextSize(20);
        lineChart.invalidate();

        List<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry(
                "学习",Legend.LegendForm.SQUARE,14,5,null,rgb(255, 154, 162)));
        legendEntries.add(new LegendEntry(
                "睡眠",Legend.LegendForm.SQUARE,14,9,null,rgb(255, 180, 71)));
        legendEntries.add(new LegendEntry(
                "用餐",Legend.LegendForm.SQUARE,14,9,null,rgb(254, 228, 64)));
        legendEntries.add(new LegendEntry(
                "摸鱼",Legend.LegendForm.SQUARE,14,9,null,rgb(189, 232, 118)));
        legendEntries.add(new LegendEntry(
                "运动",Legend.LegendForm.SQUARE,14,9,null,rgb(160, 221, 255)));
        legendEntries.add(new LegendEntry(
                "通勤",Legend.LegendForm.SQUARE,14,9,null,rgb(157, 132, 183)));
        lineChart.getLegend().setCustom(legendEntries);
        lineChart.getLegend().setFormSize(15);
        lineChart.getLegend().setTextSize(14);

        return rootView;
    }
    private void showDatePicker() {
        // 获取当前日期
//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
        // 创建日期选择器对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // 处理选定的日期
                // 在这里可以将选定的日期传递给Activity或进行其他处理
                // year: 年份
                // month: 月份（注意：0表示1月，11表示12月）
                // dayOfMonth: 日期
            }
        }, year, month, day);

        // 显示日期选择器对话框
        datePickerDialog.show();
    }
}