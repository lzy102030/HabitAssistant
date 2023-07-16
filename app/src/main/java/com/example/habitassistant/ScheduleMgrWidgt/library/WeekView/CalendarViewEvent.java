package com.example.habitassistant.ScheduleMgrWidgt.library.WeekView;

import com.example.habitassistant.utils.TimeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewEvent implements Comparable {
    public Calendar startTime = Calendar.getInstance();
    public Calendar endTime = Calendar.getInstance();
    public String name;
    public int color = ScheduleView.DEFAULT_EVENT_COLOR;
    public boolean isAllDay;
    public String content;
    public Object mdata;
    public String start_time ="";
    public String end_time = "";


    public CalendarViewEvent(String startTime2, String endTime2, String title)  {

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

        Date date1 = TimeConverter.convertTimeToDate(startTime2);
        Date date2 = TimeConverter.convertTimeToDate(endTime2);

        int year=0;
        int month=0;
        int day=0;
        int hour = 0;
        int minute = 0;
        int second=0;


        year=date1.getYear()+1;
        month=date1.getMonth();
        day=date1.getDate();
        hour = date1.getHours();
        minute = date1.getMinutes();
        second=date1.getSeconds();

        start_time=startTime2;
        end_time=endTime2;
        Calendar st = Calendar.getInstance();
        st.set(Calendar.YEAR, year);
        st.set(Calendar.MONTH, month);
        st.set(Calendar.DATE, day);
        st.set(Calendar.HOUR_OF_DAY,hour );
        st.set(Calendar.MINUTE, minute);
        st.set(Calendar.SECOND, second);

        int i = st.get(Calendar.HOUR_OF_DAY);

        year=date2.getYear()+1;
        month=date2.getMonth();
        day=date2.getDate();
        hour = date2.getHours();
        minute = date2.getMinutes();
        second=date2.getSeconds();
        startTime=st;

        Calendar et = Calendar.getInstance();
        et.set(Calendar.YEAR, year);
        et.set(Calendar.MONTH, month);
        et.set(Calendar.DATE, day);
        et.set(Calendar.HOUR_OF_DAY,hour );
        et.set(Calendar.MINUTE, minute);
        et.set(Calendar.SECOND, second);

        endTime=et;

        name=title;
    }

    public CalendarViewEvent(Calendar startTime2, Calendar endTime2, String title, int color) {
        startTime=startTime2;
        endTime=endTime2;
        this.name=title;
        this.color=color;
    }

    public CalendarViewEvent(Calendar startTime1, Calendar endTime1, String title, int color, boolean isAllDay, Object obj) {
        startTime=startTime1;
        endTime=endTime1;
        this.name=title;
        this.color=color;
        this.isAllDay=isAllDay;
        this.mdata=obj;
    }

    public CalendarViewEvent(Calendar startTime1, Calendar endTime1, String title, int color, boolean isAllDay) {
        startTime=startTime1;
        endTime=endTime1;
        this.name=title;
        this.color=color;
        this.isAllDay=isAllDay;
    }


    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Boolean getAllDay() {
        return isAllDay;
    }

    public void setAllDay(Boolean allDay) {
        isAllDay = allDay;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getMdata() {
        return mdata;
    }

    public void setMdata(Object mdata) {
        this.mdata = mdata;
    }

    @Override
    public int compareTo(Object o) {
        CalendarViewEvent calendarViewEvent=(CalendarViewEvent)o;
        return ( (Long)this.startTime.getTimeInMillis() ).compareTo(calendarViewEvent.startTime.getTimeInMillis())  ;
    }
}
