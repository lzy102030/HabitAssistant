package com.example.habitassistant.utils;

import static android.graphics.Color.rgb;

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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    public BarData createBarChart(List<BarEntry> entries, String statue) {
        BarDataSet dataSet = new BarDataSet(entries, "本周"+statue+"统计");
        //设置颜色
        dataSet.setColors(new int[]{Color.parseColor("#04a6be"),}, 255);
        //设置标签
        dataSet.setStackLabels(new String[]{statue});
        BarData barData = new BarData(dataSet);

        return barData;
    }

    public LineData createLineChart(List<ILineDataSet> entries) {
        //LineDataSet dataSet = new LineDataSet(entries, "Label");
//        dataSet.setColors(new int[]{
//                Color.parseColor("#FF9AA2"),
//                Color.parseColor("#FFB447"),
//                Color.parseColor("#FEE440"),
//                Color.parseColor("#BDE876"),
//                Color.parseColor("#A0DDFF"),
//                Color.parseColor("#9D84B7"),
//        }, 255);
        //dataSet.setLineWidth(10);
        LineData lineData = new LineData(entries);
        return lineData;
    }

    public PieData createPieChart(List<PieEntry> entries) {

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                Color.parseColor("#FF9AA2"),
                Color.parseColor("#FFB447"),
                Color.parseColor("#FEE440"),
                Color.parseColor("#BDE876"),
                Color.parseColor("#A0DDFF"),
                Color.parseColor("#9D84B7"),
        }, 255);
        dataSet.setValueTextColor(rgb(0, 0, 0));
        dataSet.setValueTextSize(17);
        //设置描述的位置
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueTextSize(15);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueLinePart1Length(0.6f);//设置描述连接线长度
        //设置数据的位置
//        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        dataSet.setValueLinePart2Length(0.6f);//设置数据连接线长度
        //设置两根连接线的颜色
        dataSet.setValueLineColor(Color.WHITE);
        PieData pieData = new PieData(dataSet);
        return pieData;
    }

    public MyXAxisValueFormatter getWeekXvalue() {
        String[] xValues = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        MyXAxisValueFormatter formatter = new MyXAxisValueFormatter(xValues);
        return formatter;
    }

    public MyXAxisValueFormatter getDayXvalue() {
        String[] xValues = new String[]{"0:00", "3:00", "6:00", "9:00", "12:00", "15:00", "18:00", "21:00", "24:00"};
        MyXAxisValueFormatter formatter = new MyXAxisValueFormatter(xValues);
        return formatter;
    }
}

