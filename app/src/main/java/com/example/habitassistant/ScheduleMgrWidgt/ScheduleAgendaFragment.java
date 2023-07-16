package com.example.habitassistant.ScheduleMgrWidgt;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.habitassistant.R;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.CalendarView;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.CalendarViewEvent;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnEventClickListener;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnEventLongPressListener;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnSchedulerPageChangedListener;
import com.example.habitassistant.utils.Constant;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAgendaFragment extends Fragment  implements CalendarPickerController {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private AgendaCalendarView agendaCalendarView;
    private List<CalendarEvent> eventList;


    public ScheduleAgendaFragment() {
        // Required empty public constructor
    }

    public static ScheduleAgendaFragment newInstance() {
        ScheduleAgendaFragment fragment = new ScheduleAgendaFragment();
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
        View rootView = inflater.inflate(R.layout.activity_schedule_agenda, container, false);
        agendaCalendarView = rootView.findViewById(R.id.agenda_calendar_view);

        //初始化监听器---可实现自定义行为


        //设置绑定监听器




        //渲染数据
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
//        maxDate.add(Calendar.YEAR, 1);
        eventList = new ArrayList<>();
        eventList.clear();
        mockList1(eventList);
        agendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);


        return rootView;
    }
    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        endTime1.add(Calendar.MONTH, 1);
        BaseCalendarEvent event1 = new BaseCalendarEvent("Thibault travels in Iceland", "A wonderful journey!", "Iceland",
                ContextCompat.getColor(getContext(), R.color.orange), startTime1, endTime1, true);
        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar endTime2 = Calendar.getInstance();
        endTime2.add(Calendar.DAY_OF_YEAR, 3);
        BaseCalendarEvent event2 = new BaseCalendarEvent("Visit to Dalvík", "A beautiful small town", "Dalvík",
                ContextCompat.getColor(getContext(), R.color.orange), startTime2, endTime2, true);
        eventList.add(event2);

        // Example on how to provide your own layout
        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.set(Calendar.HOUR_OF_DAY, 14);
        startTime3.set(Calendar.MINUTE, 0);
        endTime3.set(Calendar.HOUR_OF_DAY, 15);
        endTime3.set(Calendar.MINUTE, 0);
        DrawableCalendarEvent event3 = new DrawableCalendarEvent("Visit of Harpa", "", "Dalvík",
                ContextCompat.getColor(getContext(), R.color.blue), startTime3, endTime3, false, R.drawable.account);
        eventList.add(event3);
    }

    private void mockList1(List<CalendarEvent> eventList){
        if(Constant.eventList !=null){
            for (CalendarViewEvent event:Constant.eventList){
                DrawableCalendarEvent event2 = new DrawableCalendarEvent(event.name, "A beautiful small town", "Dalvík",
                        ContextCompat.getColor(getContext(), R.color.orange), event.startTime, event.endTime, false, R.drawable.account);
                eventList.add(event2);
            }
        }
    }
    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}
