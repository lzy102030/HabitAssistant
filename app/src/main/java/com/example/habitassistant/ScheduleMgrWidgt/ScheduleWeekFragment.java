package com.example.habitassistant.ScheduleMgrWidgt;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.habitassistant.R;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.CalendarView;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.CalendarViewEvent;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnEventClickListener;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnEventLongPressListener;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnSchedulerPageChangedListener;
import com.example.habitassistant.utils.ChartHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleWeekFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private OnEventClickListener clickListener;
    private OnEventLongPressListener longclickListener;
    private ArrayList<CalendarViewEvent> eventList = new ArrayList<CalendarViewEvent>();

    public ScheduleWeekFragment() {
        // Required empty public constructor
    }

    public static ScheduleWeekFragment newInstance() {
        ScheduleWeekFragment fragment = new ScheduleWeekFragment();
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
        View rootView = inflater.inflate(R.layout.activity_schedule_week, container, false);
        CalendarView schedule_week = rootView.findViewById(R.id.schedule_week);

        //初始化监听器---可实现自定义行为
        clickListener=new OnEventClickListener() {
            @Override
            public void eventClick(CalendarViewEvent event) {
                Log.d("ScheduleWeekFragment","OnCreateView执行定义OnEventClickListener");
            }
        };

        longclickListener=new OnEventLongPressListener() {
            @Override
            public void eventLongPress(CalendarViewEvent event) {
                Log.d("ScheduleWeekFragment","OnCreateView执行定义OnEventClickListener");
            }
        };

        //设置绑定监听器
        schedule_week.setOnEventClickListener(clickListener);
        schedule_week.setOnEventLongPressListener(longclickListener);
        schedule_week.setOnSchedulerPageChangedListener(new OnSchedulerPageChangedListener(){
            @Override
            public void changed(List<Date> showDays) {
                Log.i("SchedulerActivity", "showDays:${showDays.size}");
            }
        });


        //渲染数据
        eventList.clear();
        eventList.addAll(obtainEvents());
        schedule_week.addEvents(eventList);


        return rootView;
    }
    private ArrayList<CalendarViewEvent>  obtainEvents(){
        Calendar startTime1 = Calendar.getInstance();
        startTime1.set(Calendar.HOUR_OF_DAY, 13);
        startTime1.set(Calendar.MINUTE, 0);
        Calendar endTime1 = (Calendar)startTime1.clone();
        endTime1.set(Calendar.HOUR_OF_DAY, 15);
        endTime1.set(Calendar.MINUTE, 0);
        CalendarViewEvent event1 = new CalendarViewEvent(startTime1, endTime1, "第一个事件", Color.BLUE);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.set(Calendar.HOUR_OF_DAY, 14);
        startTime2.set(Calendar.MINUTE, 0);
        Calendar endTime2 =(Calendar) startTime2.clone();
        endTime2.set(Calendar.HOUR_OF_DAY, 15);
        endTime2.set(Calendar.MINUTE, 0);
        CalendarViewEvent event2 = new CalendarViewEvent(startTime2, endTime2, "第2个事件", Color.RED);

        Calendar startTime3 = Calendar.getInstance();
        startTime3.set(Calendar.HOUR_OF_DAY, 14);
        startTime3.set(Calendar.MINUTE, 0);
        Calendar endTime3 = (Calendar)startTime3.clone() ;
        endTime3.set(Calendar.HOUR_OF_DAY, 18);
        endTime3.set(Calendar.MINUTE, 0);
        CalendarViewEvent event3 = new CalendarViewEvent( startTime3, endTime3, "第3个事件", Color.GREEN);

        Calendar startTime4 = Calendar.getInstance();
        startTime4.set(Calendar.HOUR_OF_DAY, 16);
        startTime4.set(Calendar.MINUTE, 0);
        Calendar endTime4 = (Calendar)startTime4.clone();
        endTime4.set(Calendar.HOUR_OF_DAY, 17);
        endTime4.set(Calendar.MINUTE, 0);
        CalendarViewEvent event4 =new  CalendarViewEvent(startTime4, endTime4, "第4个事件", Color.YELLOW);


        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 13);
        startTime.set(Calendar.MINUTE, 0);
        Calendar endTime = (Calendar)startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 15);
        endTime.set(Calendar.MINUTE, 0);
        CalendarViewEvent event = new CalendarViewEvent(startTime, endTime, "全天事件", Color.GRAY, true, "这个是数据");

        Calendar startTime21 = Calendar.getInstance();
        startTime21.set(Calendar.HOUR_OF_DAY, 13);
        startTime21.set(Calendar.MINUTE, 0);
        Calendar endTime21 = (Calendar)startTime21.clone();
        endTime21.set(Calendar.HOUR_OF_DAY, 15);
        endTime21.set(Calendar.MINUTE, 0);
        endTime21.add(Calendar.DAY_OF_YEAR, 2);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 5);//6月;
        cal.set(Calendar.DAY_OF_MONTH, 21);
        CalendarViewEvent event21 = new CalendarViewEvent( startTime21, endTime21, "21全天事件", Color.MAGENTA, true, cal);

        Calendar startTime22 = Calendar.getInstance();
        startTime22.add(Calendar.DAY_OF_YEAR, 2);
        startTime22.set(Calendar.HOUR_OF_DAY, 13);
        startTime22.set(Calendar.MINUTE, 0);
        Calendar endTime22 = (Calendar)startTime22.clone();
        endTime22.set(Calendar.HOUR_OF_DAY, 15);
        endTime22.set(Calendar.MINUTE, 0);
        CalendarViewEvent event22 = new CalendarViewEvent(startTime22, endTime22, "22全天事件", Color.MAGENTA, true);

        ArrayList<CalendarViewEvent> events=new  ArrayList<CalendarViewEvent>();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event);
        events.add(event21);
        events.add(event22);
        return events;
    }


}