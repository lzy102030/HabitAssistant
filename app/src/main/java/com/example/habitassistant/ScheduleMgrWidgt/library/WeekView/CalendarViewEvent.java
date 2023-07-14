package com.example.habitassistant.ScheduleMgrWidgt.library.WeekView;

import java.util.Calendar;

public class CalendarViewEvent implements Comparable {
    public Calendar startTime = Calendar.getInstance();
    public Calendar endTime = Calendar.getInstance();
    public String name;
    public int color = ScheduleView.DEFAULT_EVENT_COLOR;
    public boolean isAllDay;
    public String content;
    public Object mdata;

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
