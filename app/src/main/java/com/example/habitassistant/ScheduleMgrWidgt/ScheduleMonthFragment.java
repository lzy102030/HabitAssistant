package com.example.habitassistant.ScheduleMgrWidgt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.habitassistant.R;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleMonthFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;


    private CalendarView calendarView;


    public ScheduleMonthFragment() {
        // Required empty public constructor
    }

    public static ScheduleMonthFragment newInstance() {
        ScheduleMonthFragment fragment = new ScheduleMonthFragment();
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
        View rootView = inflater.inflate(R.layout.activity_schedule_month, container, false);
        CalendarView calendarView = rootView.findViewById(R.id.calendarView);

        //初始化监听器---可实现自定义行为


        //设置绑定监听器




        //渲染数据


        return rootView;
    }

}
