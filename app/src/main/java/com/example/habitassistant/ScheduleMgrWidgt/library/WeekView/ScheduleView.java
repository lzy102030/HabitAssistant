package com.example.habitassistant.ScheduleMgrWidgt.library.WeekView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;


import com.example.habitassistant.utils.UnitUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduleView extends View {

    private String TAG = "ScheduleView";

    public static int VIEWTYPE_WEEK = 0;
    public static int VIEWTYPE_DAY = 1;
    public static int DEFAULT_EVENT_COLOR = Color.rgb(174, 208, 238);

    private static enum Direction {
        NONE,
        LEFT,
        RIGHT,
        VERTICAL;
    }


    private String TIME_FORMAT_SMAPLE = "00:00";
    private String  TIME_FORMAT = "hh:mm";
    private int viewType = VIEWTYPE_WEEK;
    private Context mContext;
    private int mPosition = 0;

    private ArrayList<Date> mShowDayList = new ArrayList<Date>();

    //左边时间列表画笔
    private Paint mTimeTextPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    //左边时间列宽度
    private float mTimeTextWidth= 0f;
    private float mTimeTextHeight= 0f;
    //字体大小 左边时间列
    private int mTimeTextSize = 12;
    private int mTimeColumnTextColor = Color.BLACK;
    //字体颜色
    private int mHeaderColumnTextColor = Color.BLACK;
    //字体大小  头部周、日期等字体
    private int mHeaderTextSize = 12;
    private float mHeaderColumnPadding = 10;
    private float mHeaderMarginBottom = 0f;
    private float mHeaderColumnWidth = 0f;
    //头部文字画笔
    private Paint mHeaderTextPaint=new  Paint(Paint.ANTI_ALIAS_FLAG);
    private float mHeaderTextHeight = 0f;
    private float mHeaderTextGap = 10;
    //头部背景画笔
    private Paint mHeaderBackgroundPaint=new Paint();
    private int mHeaderRowBackgroundColor = Color.WHITE;
    //day 背景画笔
    private Paint mDayBackgroundPaint=new Paint();
    private int mDayBackgroundColor = Color.rgb(245, 245, 245);
    //未来时间段的背景画笔
    private Paint mFutureBackgroundPaint=new Paint();
    private int mFutureBackgroundColor = Color.rgb(252, 252, 252);
    //已经过去的时间段背景画笔
    private Paint mPastBackgroundPaint=new Paint();
    private int mPastBackgroundColor = Color.rgb(244, 244, 244);


//    private val mFutureWeekendBackgroundPaint: Paint by lazy { Paint() }
//    private var mFutureWeekendBackgroundColor = 0
//    //
//    private val mPastWeekendBackgroundPaint: Paint by lazy { Paint() }
//    private var mPastWeekendBackgroundColor = 0

    //每小时的分割线的画笔
    private Paint mHourSeparatorPaint=new Paint();
    private int mHourSeparatorHeight = 2;
    private int mHourSeparatorColor = Color.rgb(230, 230, 230);
    //当前时间线的画笔
    private Paint mNowLinePaint=new Paint();
    private int mNowLineThickness = 5;
    private int mNowLineColor = Color.rgb(51, 127, 246);

    //@date 2018-6-7 去掉今天背景色
//    private val mTodayBackgroundPaint: Paint by lazy { Paint() }
//    private var mTodayBackgroundColor = Color.rgb(239, 247, 254)

    private Paint mTodayHeaderTextPaint=new Paint(Paint.ANTI_ALIAS_FLAG) ;
    private int mTodayHeaderTextColor = Color.rgb(39, 137, 228);
    //事件背景画笔
    private Paint mEventBackgroundPaint=new Paint();
    //头部日历背景画笔
    private Paint mHeaderColumnBackgroundPaint=new Paint();
    private int mHeaderColumnBackgroundColor = Color.WHITE;
    //
    private Paint mEventTextPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG) ;
    private int mEventTextColor = Color.BLACK;
    private int mEventTextSize = 12;
    private int mDefaultEventColor = Color.rgb(217, 227, 242);

    private int mHourHeight = 50;
    //每天的宽度
    private float mWidthPerDay= 0f;
    private int mColumnGap = 10;
    private int mNumberOfVisibleDays = 7; //天数 一周 或者 一日

    //全天事件 高度
    private int mAllDayEventHeight = 100;
    //头部高度 mHeaderTextHeight + mAllDayEventHeight
    private float mHeaderHeight= 0f;
    private int mHeaderRowPadding = 10;

    private boolean mAreDimensionsInvalid = true;
    private double mScrollToHour = -1.0;
    private boolean mShowNowLine = true;
    private int mFirstDayOfWeek = Calendar.SUNDAY;

    //滚动点
    private PointF mCurrentOrigin = new PointF(0f, 0f);

    private ArrayList<ScheduleViewEventDrawBO> eventList =new  ArrayList<ScheduleViewEventDrawBO>();

    //interface
    private OnEventClickListener eventClickListener = null;
    private OnEventLongPressListener eventLongPressListener = null;


    private int mMinimumFlingVelocity = 0;
    private int mScaledTouchSlop = 0;
    private GestureDetectorCompat mGestureDetector= null;
    private OverScroller mScroller= null;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private Direction mCurrentScrollDirection = Direction.NONE;
    private boolean mHorizontalFlingEnabled = true;
    private boolean mVerticalFlingEnabled = true;

    //TODO 添加GestureDetector.SimpleOnGestureListener
    private GestureDetector.SimpleOnGestureListener mGestureListener=new GestureDetector.SimpleOnGestureListener(){
        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
            if (e != null && eventLongPressListener != null) {
                eventList.forEach ( event ->{
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        eventLongPressListener.eventLongPress(event.event);
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        return;
                    }
                });
            }
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (e != null && eventClickListener != null) {
                eventList.forEach ( event ->{
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        eventClickListener.eventClick(event.event);
                        playSoundEffect(SoundEffectConstants.CLICK);
                    }
                });
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            if (mCurrentScrollDirection == Direction.NONE) {
                // Allow scrolling only in one direction.
                if (Math.abs(distanceX) < Math.abs(distanceY)) {
                    mCurrentScrollDirection = Direction.VERTICAL;
                }
            }

            // Calculate the new origin after scroll.
            if(mCurrentScrollDirection== Direction.VERTICAL ){
                mCurrentOrigin.y -= distanceY;
                ViewCompat.postInvalidateOnAnimation(ScheduleView.this);
                return true;
            }else {
                return false;
            }
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            if (mCurrentFlingDirection == Direction.LEFT && !mHorizontalFlingEnabled ||
                    mCurrentFlingDirection == Direction.RIGHT && !mHorizontalFlingEnabled ||
                    mCurrentFlingDirection == Direction.VERTICAL && !mVerticalFlingEnabled) {
                return false;
            }

            mScroller.forceFinished(true);

            mCurrentFlingDirection = mCurrentScrollDirection;
            if (mCurrentFlingDirection == Direction.VERTICAL) {
                mScroller.fling(
                        (int) mCurrentOrigin.x,
                        (int) mCurrentOrigin.y,
                        0,
                        (int) velocityY,
                        Integer.MIN_VALUE, Integer.MAX_VALUE,
                        (int) (-((float) (mHourHeight * 24) + mHeaderHeight + (float) (mHeaderRowPadding * 2) + mHeaderMarginBottom + mTimeTextHeight / 2 - getHeight())),
                        0
                );
                ViewCompat.postInvalidateOnAnimation(ScheduleView.this);
                return true;
            }
            return false;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            // Reset scrolling and fling direction.
            mCurrentFlingDirection = Direction.NONE;
            mCurrentScrollDirection = Direction.NONE;
            return true;
        }
    };
    public ScheduleView(Context context) {
        this(context, null, 0);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attributes, int defStyleAttr) {

        super(context, attributes, defStyleAttr);
        mContext = context;
        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);
        mScroller = new OverScroller(mContext, new FastOutLinearInInterpolator());

        mMinimumFlingVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    //  Integer firstDayOfWeek = Calendar.SUNDAY
    public ScheduleView(Context context, int viewType,
                        Integer headerTextSize,
                        Integer headerTextColor ,
                        Integer todayTextColor ,
                        Integer timeTextSize ,
                        Integer timeTextColor,
                        Integer eventTextSize ,
                        Integer hourHeight ,
                        Integer firstDayOfWeek)   {

        this(context);

        if(headerTextSize==null){
            mHeaderTextSize = UnitUtils.sp2px(context,14);
        }else{
            mHeaderTextSize = headerTextSize;
        }

        if(headerTextColor==null){
            mHeaderColumnTextColor =  Color.BLACK;
        }else{
            mHeaderColumnTextColor = headerTextColor;
        }


        if(todayTextColor==null){
            mTodayHeaderTextColor =  Color.rgb(39, 137, 228);
        }else{
            mTodayHeaderTextColor = todayTextColor ;
        }

        if(timeTextSize==null){
            mTimeTextSize = UnitUtils.sp2px(context,14);//sp
        }else{
            mTimeTextSize = timeTextSize;
        }

        if(timeTextColor==null){
            mTimeColumnTextColor =Color.BLACK;
        }else{
            mTimeColumnTextColor = timeTextColor;
        }


        if(eventTextSize==null){
            mEventTextSize =UnitUtils.sp2px(context,10);
        }else{
            mEventTextSize = eventTextSize;
        }

        mFirstDayOfWeek = firstDayOfWeek;

        if(hourHeight==null){
            mHourHeight = UnitUtils.dip2px(context,50);
        }else{
            mHourHeight = hourHeight ;
        }


        //下面的参数暂时不开放设置
        mAllDayEventHeight =  UnitUtils.dip2px(context,50);
        mHeaderRowPadding =  UnitUtils.dip2px(context,8);
        mColumnGap =  UnitUtils.dip2px(context,0);
        mHeaderTextGap =  UnitUtils.dip2px(context,5);
        mHeaderColumnPadding =  UnitUtils.dip2px(context,10);
        mEventTextColor = Color.WHITE;

        this.viewType = viewType;
        if(viewType==VIEWTYPE_DAY){
            mNumberOfVisibleDays =1;
        }else{
            mNumberOfVisibleDays =7;
        }
    }

    /**
     * 初始化方法 必须执行
     */
    public void initDrawThing() {
        //init 时间列
        mTimeTextPaint.setColor( mTimeColumnTextColor);
        mTimeTextPaint.setTextAlign( Paint.Align.RIGHT);
        mTimeTextPaint.setTextSize((float)mTimeTextSize);

        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds(TIME_FORMAT_SMAPLE, 0, TIME_FORMAT_SMAPLE.length(), rect);
        mTimeTextHeight = (float)rect.height();
        mHeaderMarginBottom = mTimeTextHeight / 2;
        mTimeTextWidth = mTimeTextPaint.measureText(TIME_FORMAT_SMAPLE);
        //init 头部
        // Measure settings for header row.
        mHeaderTextPaint.setColor(mHeaderColumnTextColor);
        mHeaderTextPaint.setTextAlign( Paint.Align.CENTER);
        mHeaderTextPaint.setTextSize((float)mHeaderTextSize);
        mHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mHeaderTextPaint.getTextBounds(TIME_FORMAT_SMAPLE, 0, TIME_FORMAT_SMAPLE.length(), rect);
        mHeaderTextHeight = (float) rect.height() * 2 + mHeaderTextGap;

        // Prepare header background paint.
        mHeaderBackgroundPaint.setColor(mHeaderRowBackgroundColor);

        // Prepare day background color paint.
        mDayBackgroundPaint.setColor(mDayBackgroundColor);
        mFutureBackgroundPaint.setColor(mFutureBackgroundColor);
        mPastBackgroundPaint.setColor(mPastBackgroundColor);

//        mFutureWeekendBackgroundPaint.color = mFutureWeekendBackgroundColor
//        mPastWeekendBackgroundPaint.color = mPastWeekendBackgroundColor

        // Prepare hour separator color paint.
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth((float)mHourSeparatorHeight);
        mHourSeparatorPaint.setColor(mHourSeparatorColor);

        // Prepare the "now" line color paint
        mNowLinePaint.setStrokeWidth((float)mNowLineThickness);
        mNowLinePaint.setColor(mNowLineColor);

        // Prepare today background color paint.
//        mTodayBackgroundPaint.color = mTodayBackgroundColor

        // Prepare today header text color paint.

        mTodayHeaderTextPaint.setColor(mTodayHeaderTextColor);
        mTodayHeaderTextPaint.setTextAlign( Paint.Align.CENTER);
        mTodayHeaderTextPaint.setTextSize((float)mHeaderTextSize);
        mTodayHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);


        // Prepare event background color.
        mEventBackgroundPaint.setColor(DEFAULT_EVENT_COLOR);

        // Prepare header column background color.
        mHeaderColumnBackgroundPaint.setColor( mHeaderColumnBackgroundColor);

        // Prepare event text size and color.
        mEventTextPaint.setColor(mEventTextColor);
        mEventTextPaint.setStyle(Paint.Style.FILL);
        mEventTextPaint.setTextSize((float)mEventTextSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the header row.
        drawHeaderRowAndEvents(canvas);

        //draw time column
        drawTimeColumnAndAxes(canvas);
    }


    /********************************
     * private
     */

    private void drawTimeColumnAndAxes(Canvas canvas) {
        // Draw the background color for the header column.
        canvas.drawRect(0f, mHeaderHeight + mHeaderRowPadding * 2, mHeaderColumnWidth,(float)getHeight(), mHeaderColumnBackgroundPaint);
        // Clip to paint in left column only.
        canvas.save();
        canvas.clipRect(0f, mHeaderHeight + mHeaderRowPadding * 2, mHeaderColumnWidth, (float)getHeight());
        for (int i=0;i<24;i++) {
            float top = mHeaderHeight + (float)(mHeaderRowPadding * 2) + mCurrentOrigin.y + (float)(mHourHeight * i) + mHeaderMarginBottom;
            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            String time = i < 10? "0"+String.valueOf(i)+":00" : String.valueOf(i)+":00";
            if (top < getHeight()) {
                canvas.drawText(time, mTimeTextWidth + mHeaderColumnPadding * 2, top + mTimeTextHeight, mTimeTextPaint);
            }
        }
        canvas.restore();
    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        mHeaderColumnWidth = mTimeTextWidth + mHeaderColumnPadding * 2;
        mWidthPerDay = (float) getWidth() - mHeaderColumnWidth - (float) (mColumnGap * (mNumberOfVisibleDays - 1));
        mWidthPerDay /= mNumberOfVisibleDays;
        calculateHeaderHeight();

        if (mAreDimensionsInvalid) {
            mAreDimensionsInvalid = false;
            if (mScrollToHour >= 0) {
                goToHour(mScrollToHour);
                mScrollToHour = -1.0;
                mAreDimensionsInvalid = false;
            }
        }

        if (mCurrentOrigin.y < (float)getHeight() - (float)(mHourHeight * 24) - mHeaderHeight - (float)(mHeaderRowPadding * 2) - mHeaderMarginBottom - mTimeTextHeight / 2) {
            mCurrentOrigin.y = (float)getHeight() - (float)(mHourHeight * 24) - mHeaderHeight - (float)(mHeaderRowPadding * 2) - mHeaderMarginBottom - mTimeTextHeight / 2;
        }
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0f;
        }

//        canvas?.clipRect(mHeaderColumnWidth,
//                mHeaderHeight + (mHeaderRowPadding * 2).toFloat() + mHeaderMarginBottom + mTimeTextHeight / 2,
//                width.toFloat(),
//                height.toFloat(),
//                Region.Op.REPLACE)
        canvas.save();
        canvas.clipRect(mHeaderColumnWidth,
                mHeaderHeight + (float)(mHeaderRowPadding * 2)+ mHeaderMarginBottom + mTimeTextHeight / 2,
                (float)getWidth(),
                (float)getHeight());


         float startPixel = mHeaderColumnWidth;
         final float[]  finalStartPixel={startPixel};

        int lineCount = (int)(((float)getHeight() - mHeaderHeight - (float)(mHeaderRowPadding * 2) -
                mHeaderMarginBottom) / mHourHeight) + 1;
        lineCount *= (mNumberOfVisibleDays + 1);
        float[] hourLines = new float[lineCount * 4];

        mShowDayList.forEach ( (date) ->
        {
            boolean isToday = isToday(date);
            float start = finalStartPixel[0];

            //计算时间段 是过去时间还是未来时间
            float startY = mHeaderHeight + (float)(mHeaderRowPadding * 2) + mTimeTextHeight / 2 + mHeaderMarginBottom + mCurrentOrigin.y;
            if(isToday){
                Calendar now = Calendar.getInstance();
                float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight;
                canvas.drawRect(start, startY, finalStartPixel[0] + mWidthPerDay, startY + beforeNow, mPastBackgroundPaint);
                canvas.drawRect(start, startY + beforeNow, finalStartPixel[0] + mWidthPerDay, (float)getHeight(), mFutureBackgroundPaint);
            }

            if(date.before(new Date())){
                canvas.drawRect(start, startY, finalStartPixel[0] + mWidthPerDay, (float)getHeight(), mPastBackgroundPaint);
            }else{
                canvas.drawRect(start, startY, finalStartPixel[0] + mWidthPerDay,(float)getHeight(), mFutureBackgroundPaint);
            }

            int i = 0;
            for (int hourNumber=0;hourNumber<24;hourNumber++) {
                float top = mHeaderHeight + (float)(mHeaderRowPadding * 2) + mCurrentOrigin.y + (float)(mHourHeight * hourNumber) + mTimeTextHeight / 2 + mHeaderMarginBottom;
                if (top > mHeaderHeight + (float)(mHeaderRowPadding * 2) + mTimeTextHeight / 2 + mHeaderMarginBottom - mHourSeparatorHeight
                        && top < getHeight() && finalStartPixel[0] + mWidthPerDay - start > 0) {
                    hourLines[i * 4] = start;
                    hourLines[i * 4 + 1] = top;
                    hourLines[i * 4 + 2] = finalStartPixel[0] + mWidthPerDay;
                    hourLines[i * 4 + 3] = top;
                    i++;
                }
             }

            // Draw the lines for hours.
            canvas.drawLines(hourLines, mHourSeparatorPaint);
            // Draw the events.
            drawEvents(date, finalStartPixel[0], canvas);
            // Draw the line at the current time.
            if (mShowNowLine && isToday) {
                //val startY = mHeaderHeight + (mHeaderRowPadding * 2).toFloat() + mTimeTextHeight / 2 + mHeaderMarginBottom + mCurrentOrigin.y
                Calendar now = Calendar.getInstance();
                float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight;
                canvas.drawLine(start, startY + beforeNow, finalStartPixel[0] + mWidthPerDay, startY + beforeNow, mNowLinePaint);
            }
            finalStartPixel[0]=finalStartPixel[0]+mWidthPerDay + mColumnGap;

        });

        startPixel =  finalStartPixel[0];
        canvas.restore();


//        canvas?.clipRect(0f, 0f, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE)
        canvas.save();
        canvas.clipRect(0f, 0f, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2);
        canvas.drawRect(0f, 0f, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);
        canvas.restore();

        // Draw all day label
        if (mHeaderHeight > mHeaderTextHeight) { //have all day event
            canvas.drawText("全天", mTimeTextWidth + mHeaderRowPadding*2, mHeaderHeight, mTimeTextPaint);
        }

        // Clip to paint header row only.
        canvas.save();
        canvas.clipRect(mHeaderColumnWidth, 0f, (float) getWidth(), mHeaderHeight + mHeaderRowPadding * 2);
        canvas.drawRect(0f, 0f,(float) getWidth(), mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);
        canvas.restore();

        startPixel = mHeaderColumnWidth;
        final float[]  finalStartPixel1={startPixel};
        mShowDayList.forEach ( (date) ->{
            boolean isToday = isToday(date);
            String weekDayCh = getWeekDay(date);
            String dayString = stringDateTime("d", date);

            if (viewType == VIEWTYPE_WEEK) {
                canvas.drawText(weekDayCh, finalStartPixel1[0] + mWidthPerDay / 2, mHeaderTextHeight / 2 + mHeaderRowPadding, isToday ? mTodayHeaderTextPaint : mHeaderTextPaint);
                canvas.drawText(dayString, finalStartPixel1[0] + mWidthPerDay / 2, mHeaderTextHeight + mHeaderRowPadding + mHeaderTextGap, isToday ? mTodayHeaderTextPaint : mHeaderTextPaint);

            } else if (viewType == VIEWTYPE_DAY) {
                float y = (mHeaderTextHeight + mHeaderRowPadding + mHeaderTextGap) - ((mHeaderTextHeight + mHeaderRowPadding + mHeaderTextGap) - (mHeaderTextHeight / 2)) / 2;
                canvas.drawText(weekDayCh+" / "+ dayString, finalStartPixel1[0] + mWidthPerDay / 2, y, isToday ? mTodayHeaderTextPaint : mHeaderTextPaint);
            }

            drawAllDayEvents(date, finalStartPixel1[0] , canvas);
            finalStartPixel1[0]  += mWidthPerDay + mColumnGap;
        });
        startPixel =finalStartPixel1[0];

    }

    private void  drawAllDayEvents(Date date, Float startPixel, Canvas canvas) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        eventList.forEach(eventDrawBO ->{
            if ((isSameDay(eventDrawBO.event.startTime, day) || theAllDayEventCanDraw(day, eventDrawBO.event.startTime, eventDrawBO.event.endTime)) && eventDrawBO.event.isAllDay) {
                float top = mHeaderTextHeight + mHeaderRowPadding * 2 + mHeaderTextGap + eventDrawBO.top * mAllDayEventHeight;
                float bottom = top + eventDrawBO.height * mAllDayEventHeight;
                float right = startPixel + eventDrawBO.right * mWidthPerDay;
                if (right > getWidth()) {
                    right = (float)getWidth();
                }
                RectF rectF = new RectF(startPixel, top, right, bottom);
                eventDrawBO.rectF = rectF;
                int  eventColor =eventDrawBO.event.color == 0? mDefaultEventColor : eventDrawBO.event.color;
                mEventBackgroundPaint.setColor(eventColor);
                canvas.drawRoundRect(rectF, 0f, 0f, mEventBackgroundPaint);
                boolean isDark =false; //TODO Ext.isDarkColor(eventColor);
                drawEventTitle(eventDrawBO.event, rectF, canvas, top, startPixel, isDark);
            }
        });
    }

    private void drawEvents(Date date, Float startPixel, Canvas canvas) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        eventList.forEach(eventDrawBO ->{
            if (isSameDay(eventDrawBO.event.startTime, day) && !eventDrawBO.event.isAllDay) {
                // Calculate top.
                float top = (float)mHourHeight * 24f * eventDrawBO.top / 1440 +
                        mCurrentOrigin.y + mHeaderHeight + (float)(mHeaderRowPadding * 2) +
                        mHeaderMarginBottom + mTimeTextHeight / 2;
                // Calculate bottom.
                float bottom = (float)mHourHeight * 24f * eventDrawBO.bottom / 1440 +
                        mCurrentOrigin.y + mHeaderHeight + (float)(mHeaderRowPadding * 2) +
                        mHeaderMarginBottom + mTimeTextHeight / 2;
                // Calculate left and right.
                float left = startPixel + eventDrawBO.left * mWidthPerDay;
                float right = left + eventDrawBO.width * mWidthPerDay;
                if (left < right &&
                        left < getWidth() &&
                        top < getHeight() &&
                        right > mHeaderColumnWidth &&
                        bottom > mHeaderHeight + (float) (mHeaderRowPadding * 2) + mTimeTextHeight / 2 + mHeaderMarginBottom) {
                    RectF rectF = new RectF(left, top, right, bottom);
                    eventDrawBO.rectF = rectF;
                    int eventColor = eventDrawBO.event.color == 0? mDefaultEventColor : eventDrawBO.event.color;
                    mEventBackgroundPaint.setColor(eventColor);
                    canvas.drawRoundRect(rectF, 0f, 0f, mEventBackgroundPaint);
                    boolean isDarkColor = false;//TODO Ext.isDarkColor(eventColor)
                    drawEventTitle(eventDrawBO.event, rectF, canvas, top, left, isDarkColor);
                }
            }
        });
    }

    /**
     * 事件的标题
     * todo 如果背景色偏淡 字体颜色设为黑色 否则白色
     */
    private void drawEventTitle(CalendarViewEvent event, RectF rect, Canvas canvas, Float originalTop , Float originalLeft, Boolean isEventColorDark) {
        int mEventPadding = 8;
        if (rect.right - rect.left - (float) (mEventPadding * 2) < 0) {
            return;
        }

        if (rect.bottom - rect.top - (float)(mEventPadding * 2) < 0) {
            return;
        }
        // Prepare the name of the event.
        SpannableStringBuilder bob = new SpannableStringBuilder();
        bob.append(event.name);
        bob.setSpan(new StyleSpan(Typeface.BOLD), 0, bob.length(), 0);
        bob.append(' ');

        int textColor = isEventColorDark? mEventTextColor : Color.BLACK;
        mEventTextPaint.setColor(textColor);

        int availableHeight = (int)(rect.bottom - originalTop - (float)(mEventPadding * 2));
        int availableWidth = (int)(rect.right - originalLeft - (float)(mEventPadding * 2));

        // Get text dimensions.
        StaticLayout textLayout = new StaticLayout(bob, (TextPaint) mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        double lineHeight = textLayout.getHeight() / textLayout.getLineCount();

        if (availableHeight >= lineHeight) {
            // Calculate available number of line counts.
            int availableLineCount = (int) (availableHeight / lineHeight);
            do {
                // Ellipsize text to fit into event rect.
                textLayout = new StaticLayout(TextUtils.ellipsize(bob, (TextPaint) mEventTextPaint, (float) (availableLineCount * availableWidth), TextUtils.TruncateAt.END),
                        (TextPaint) mEventTextPaint,
                        (int)(rect.right - originalLeft - (float) (mEventPadding * 2)),
                        Layout.Alignment.ALIGN_NORMAL,
                        1.0f,
                        0.0f,
                        false);

                // Reduce line count.
                availableLineCount--;

                // Repeat until text is short enough.
            } while (textLayout.getHeight() > availableHeight);

            // Draw text.
            canvas.save();
            canvas.translate(originalLeft + mEventPadding, originalTop + mEventPadding);
            textLayout.draw(canvas);
            canvas.restore();
        }
    }


    private void calculateHeaderHeight() {
        //Make sure the header is the right size (depends on AllDay events)
        boolean containsAllDayEvent = false;
        //全天事件
        if (eventList.size() > 0) {
            for (int dayNumber=0;dayNumber< mShowDayList.size();dayNumber++) {
                Calendar day = Calendar.getInstance();
                day.setTime(mShowDayList.get(dayNumber)); ;
                for (int i=0;i<eventList.size();i++) {
                    boolean can = theAllDayEventCanDraw(day, eventList.get(i).event.startTime, eventList.get(i).event.endTime);
                    if ((isSameDay(eventList.get(i).event.startTime, day) || can) && eventList.get(i).event.isAllDay) {
                        containsAllDayEvent = true;
                        break;
                    }
                }
                if (containsAllDayEvent) {
                    break;
                }
            }
        }
        if (containsAllDayEvent) {
            mHeaderHeight =   mHeaderTextHeight + (mAllDayEventHeight + mHeaderMarginBottom);
        } else {
            mHeaderHeight = mHeaderTextHeight;
        }
    }


    /**
     * 当前日期是否需要画出这个全天事件
     */
    private Boolean theAllDayEventCanDraw(Calendar day, Calendar startDay, Calendar endDay) {
        return isFirstDay(day) && isContainDay(day, startDay, endDay);
    }

    /**
     * 当前的日期是否在事件的开始日期和结束日期之间
     */
    private Boolean isContainDay(Calendar day, Calendar startDay, Calendar endDay ){
        if(isSameDay(day, startDay)){
            return true;
        } else if (isSameDay(day,endDay)) {
            return true;
        } else if (startDay.before(day) && endDay.after(day)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 是否是当前周期的第一天
     */
    private Boolean isFirstDay(Calendar dayOne) {
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(mShowDayList.get(0)); ;
        return isSameDay(firstDay, dayOne);
    }
    private Boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    private String stringDateTime(String aMask, Date aDate) {
        String returnValue = "";
        if (aDate != null) {
            SimpleDateFormat df = new SimpleDateFormat(aMask, Locale.getDefault());
            returnValue = df.format(aDate);
        }
        return returnValue;
    }

    /**
     * 获取星期几
     * @param date
     * @return 周日 周一 周二 周三 周四 周五 周六
     * @throws ParseException
     */
    private String getWeekDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekDay = c.get(Calendar.DAY_OF_WEEK);

        switch (weekDay){
            case Calendar.SUNDAY :
                return "日";
            case Calendar.MONDAY :
                return "一";
            case Calendar.TUESDAY :
                return "二";
            case Calendar.WEDNESDAY :
                return "三";
            case Calendar.THURSDAY :
                return "四";
            case Calendar.FRIDAY :
                return "五";
            case Calendar.SATURDAY :
                return "六";
            default:
                return  "";
        }

    }
    /**
     * 判断是否为今天
     */
    private Boolean isToday(Date date){
        Calendar now = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        return now.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR);
    }

    private Boolean isEventsCollide(CalendarViewEvent event, CalendarViewEvent event1) {
        long start = event.startTime.getTimeInMillis();
        long end = event.endTime.getTimeInMillis();
        long start1 = event1.startTime.getTimeInMillis();
        long end1 = event1.endTime.getTimeInMillis();
        return !((start >= end1) || (end <= start1));

    }
    private void refreshEventsAndCalEventWidth(List<CalendarViewEvent> result) {
        eventList.clear();
        Collections.sort(result, new Comparator<CalendarViewEvent>() {
            @Override
            public int compare(CalendarViewEvent o1, CalendarViewEvent o2) {
                return o1.compareTo(o2);
            }
        });

        HashMap<String, ArrayList<CalendarViewEvent>> groups = new HashMap<String, ArrayList<CalendarViewEvent>>();
        String allDayKey = "allDayKey";

        for(CalendarViewEvent it:result){
            Log.d("ScheduleView","iterator events");
            if (it.isAllDay) {
                if (groups.containsKey(allDayKey)) {
                    groups.get(allDayKey).add(it);
                } else {
                    groups.put(allDayKey,new ArrayList<>());
                    groups.get(allDayKey).add(it);
                }
            } else {
                String date = stringDateTime("yyyy-MM-dd", it.startTime.getTime());
                if (groups.containsKey(date)) {
                    groups.get(date).add(it);
                } else {
                    groups.put(date,new ArrayList<>());
                    groups.get(date).add(it);
                }
            }
        }
        groups.forEach ( (key, value) ->{
            calEventWidthEveryday(value);
        });
    }


    private void  calEventWidthEveryday(ArrayList<CalendarViewEvent> list) {
        //开始计算事件方块的重叠情况，计算一个最终的方块宽度
        ArrayList<ArrayList<CalendarViewEvent>> columns = new ArrayList<ArrayList<CalendarViewEvent>>();
        columns.add(new ArrayList<>());
        for (CalendarViewEvent eventRect : list) {
            boolean isPlaced = false;
            for (ArrayList<CalendarViewEvent> column : columns) {
                if (column.isEmpty()) {
                    column.add(eventRect);
                    isPlaced = true;
                } else if (!isEventsCollide(eventRect, column.get(column.size() - 1))) {
                    column.add(eventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                ArrayList<CalendarViewEvent> newColumn = new ArrayList<CalendarViewEvent>();
                newColumn.add(eventRect);
                columns.add(newColumn);
            }
        }

        int maxRowCount = 0;
        for (ArrayList<CalendarViewEvent> column : columns) {
            maxRowCount = Math.max(maxRowCount, column.size());
        }

        for (int i=0;i<maxRowCount;i++) {
            // Set the left and right values of the event.
            float j = 0f;
            for (ArrayList<CalendarViewEvent> column : columns) {
                if (column.size() >= i + 1) {
                    CalendarViewEvent event = column.get(i);
                    ScheduleViewEventDrawBO eventRect = new ScheduleViewEventDrawBO(event);
                    if (!eventRect.event.isAllDay) {
                        eventRect.width = 1f / columns.size();
                        eventRect.left = j / columns.size();
                        eventRect.top = (float) (eventRect.event.startTime.get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.startTime.get(Calendar.MINUTE));
                        eventRect.bottom = (float)(eventRect.event.endTime.get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.endTime.get(Calendar.MINUTE));
                    } else {// 全天事件 还有 跨天事件 是在头部 多个事件重叠是纵向排列
                        eventRect.height = 1f / columns.size();
                        eventRect.top = j / columns.size();
                        int rightIndex = eventRect.event.endTime.get(Calendar.DAY_OF_YEAR) - eventRect.event.startTime.get(Calendar.DAY_OF_YEAR);
                        //left起始  , right是跨度 跨几天
                        eventRect.left = 0f;
                        eventRect.right = (float) (rightIndex + 1);
                    }
                    eventList.add(eventRect);
                }
                j++;
            }
        }
    }


    /********************************
     * public
     */

    /**
     * 添加日程事件
     */
    public void setEvents(List<CalendarViewEvent> result) {
        refreshEventsAndCalEventWidth(result);
        notifyDataChanged();
    }
    /**
     * 事件数据变化后重绘
     */
    private void notifyDataChanged() {
        invalidate();
    }


    public void  setOnEventClickListener(OnEventClickListener listener) {
        eventClickListener = listener;
    }

    public void  setOnEventLongPressListener(OnEventLongPressListener listener) {
        eventLongPressListener = listener;
    }



    /**
     * Vertically scroll to a specific hour in the week view.
     * @param hour The hour to scroll to in 24-hour format. Supported values are 0-24.
     */
    public void goToHour(Double hour) {
        if (mAreDimensionsInvalid) {
            mScrollToHour = hour;
            return;
        }

        int verticalOffset = 0;
        if (hour > 24){
            verticalOffset = mHourHeight * 24;
        } else if (hour > 0){
            verticalOffset = (int)(mHourHeight * hour);
        }

        if (verticalOffset > (float)(mHourHeight * 24 - getHeight())+ mHeaderHeight + (float)(mHeaderRowPadding * 2) + mHeaderMarginBottom)
            verticalOffset = (int) ((float)(mHourHeight * 24 - getHeight()) + mHeaderHeight + (float)(mHeaderRowPadding * 2) + mHeaderMarginBottom);

        mCurrentOrigin.y = (float)(-verticalOffset);
        invalidate();
    }

    /**
     * 设置当前页面的位置，position=0 就是今天
     */
    public List<Date> setScheduleViewPagerPosition(Integer position) { //控制当前日历生成
        mPosition = position;
        mShowDayList.clear();

        if (viewType == VIEWTYPE_WEEK) {
            Date positionWeekDay = addDay(new Date(), (mPosition) * 7);
            Date day0 = getFirstDayOfWeek(positionWeekDay);
            mShowDayList.add(day0);
            mShowDayList.add(addDay(day0, 1));
            mShowDayList.add(addDay(day0, 2));
            mShowDayList.add(addDay(day0, 3));
            mShowDayList.add(addDay(day0, 4));
            mShowDayList.add(addDay(day0, 5));
            mShowDayList.add(addDay(day0, 6));
        } else if (viewType == VIEWTYPE_DAY) {
            Date positionDay = addDay(new Date(), mPosition);
            mShowDayList.add(positionDay);
        }

        //设置当前滚动位置 默认滚动到6点，如果position等于1 滚动到当前时间
        if(mPosition==0){
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            goToHour((double)hour);
        }else {
            goToHour(6.0);
        }
        invalidate();
        return mShowDayList;
    }

    /**
     * 取得指定日期所在周的第一天
     *
     */
    private Date getFirstDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(mFirstDayOfWeek);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime();
    }
    private Date addDay(Date date, Integer dayCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, dayCount);// 指定的时间上加上n天
        return calendar.getTime();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean flag = mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && mCurrentFlingDirection == Direction.NONE) {
            mCurrentScrollDirection = Direction.NONE;
        }
        return flag ;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.isFinished() == true) {
            if (mCurrentFlingDirection != Direction.NONE) {
                // Snap to day after fling is finished.
                mCurrentFlingDirection = Direction.NONE;
                mCurrentScrollDirection = Direction.NONE;
            }
        } else {
            if (mCurrentFlingDirection != Direction.NONE && mMinimumFlingVelocity >= mScroller.getCurrVelocity() ) {
                mCurrentFlingDirection = Direction.NONE;
                mCurrentScrollDirection = Direction.NONE;
            } else if (mScroller.computeScrollOffset() == true) {
                mCurrentOrigin.y = (float) mScroller.getCurrY() ;
                mCurrentOrigin.x = (float) mScroller.getCurrX();
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }


}
