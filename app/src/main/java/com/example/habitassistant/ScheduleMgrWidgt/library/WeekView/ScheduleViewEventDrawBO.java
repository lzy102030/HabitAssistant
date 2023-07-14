package com.example.habitassistant.ScheduleMgrWidgt.library.WeekView;

import android.graphics.RectF;

public class ScheduleViewEventDrawBO {
    CalendarViewEvent event;
    float left = 0f;
    float right = 0f;
    float width = 0f;//普通事件 算宽度
    float height = 0f; //全天事件 算高度
    float top = 0f;
    float bottom = 0f;
    RectF rectF= null ;// 计算点击事件用的

    public ScheduleViewEventDrawBO(CalendarViewEvent event) {
        this.event=event;
    }


    public CalendarViewEvent getEvent() {
        return event;
    }

    public void setEvent(CalendarViewEvent event) {
        this.event = event;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }
}
