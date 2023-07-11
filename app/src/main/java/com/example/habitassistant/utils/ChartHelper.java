package com.example.habitassistant.utils;

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

    public BarData createBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 30));
        entries.add(new BarEntry(1, 80));
        entries.add(new BarEntry(2, 60));
        entries.add(new BarEntry(3, 50));
        // More entries...

        BarDataSet dataSet = new BarDataSet(entries, "Label");
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
        entries.add(new PieEntry(25, "Label 2"));
        entries.add(new PieEntry(45, "Label 3"));
        // More entries...

        PieDataSet dataSet = new PieDataSet(entries, "Label");
        PieData pieData = new PieData(dataSet);
        return pieData;
    }
}

