package com.example.habitassistant.utils;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    public BarData createBarChart(List<BarEntry> entries, String statue) {
        BarDataSet dataSet = new BarDataSet(entries, "今日统计");
        //设置颜色
        dataSet.setColors(new int[]{Color.parseColor("#04a6be"),}, 255);
        //设置标签
        dataSet.setStackLabels(new String[]{statue});
        BarData barData = new BarData(dataSet);

        return barData;
    }

    public LineData createLineChart() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 20));
        entries.add(new Entry(1, 50));
        entries.add(new Entry(2, 70));
        entries.add(new Entry(3, 90));
        // More entries...

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        return lineData;
    }

    public PieData createPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30, "Label 1"));
        entries.add(new PieEntry(35, "Label 2"));
        entries.add(new PieEntry(45, "Label 3"));
        // More entries...

        PieDataSet dataSet = new PieDataSet(entries, "Label");
        PieData pieData = new PieData(dataSet);
        return pieData;
    }

    public MyXAxisValueFormatter getXvalue() {
        String[] xValues = new String[]{"0:00", "3:00", "6:00", "9:00", "12:00", "15:00", "18:00", "21:00", "24:00"};
        MyXAxisValueFormatter formatter = new MyXAxisValueFormatter(xValues);
        return formatter;
    }
}

