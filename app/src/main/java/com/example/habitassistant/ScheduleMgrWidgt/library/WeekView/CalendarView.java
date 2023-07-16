package com.example.habitassistant.ScheduleMgrWidgt.library.WeekView;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.example.habitassistant.MainActivity;
import com.example.habitassistant.R;
import com.example.habitassistant.entity.DayActivityData;
import com.example.habitassistant.utils.Constant;
import com.example.habitassistant.utils.OkhttpHelper;
import com.example.habitassistant.utils.UnitUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalendarView extends LinearLayout {

    private int TODAY_HEADER_COLOR = Color.rgb(39, 137, 228);
    private int VIEW_PAGER_START_POSITION = 500;

    //自定义参数

    private int mViewType = ScheduleView.VIEWTYPE_WEEK;    //试图类型
    private int mHeaderTextSize = 0;
    private int mHeaderColumnTextColor = Color.BLACK;
    private int mTodayHeaderTextColor = TODAY_HEADER_COLOR;
    private int mTimeTextSize = 0;
    private int mTimeColumnTextColor = Color.BLACK;
    private int mEventTextSize = 0;
    private int mFirstDayOfWeek = Calendar.SUNDAY;
    private int mHourHeight = 0;

    private Context mcontext;
    private OnEventClickListener eventClickListener= null;
    private OnEventLongPressListener eventLongPressListener= null;
    private OnSchedulerPageChangedListener pageChangedListener = null;

    private ViewPager viewPager ;
    private SViewPagerAdapter adapter ;

    private ArrayList<CalendarViewEvent>  mEventList = new  ArrayList<CalendarViewEvent>() ;

    public CalendarView(Context context) {
        this(context, null, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attributes, R.styleable.CalendarView);

        mHeaderTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_headerTextSize, UnitUtils
        .sp2px(context,14));
        mHeaderColumnTextColor = array.getColor(R.styleable.CalendarView_headerTextColor, Color.BLACK);
        mTodayHeaderTextColor = array.getColor(R.styleable.CalendarView_headerTodayTextColor, TODAY_HEADER_COLOR);
        mTimeTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_timeTextSize, UnitUtils
                .sp2px(context,14));
        mTimeColumnTextColor = array.getColor(R.styleable.CalendarView_timeTextColor, Color.BLACK);
        mEventTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_eventTextSize, UnitUtils
                .sp2px(context,10));
        mViewType = array.getInt(R.styleable.CalendarView_viewType, ScheduleView.VIEWTYPE_WEEK);
        mFirstDayOfWeek = array.getInt(R.styleable.CalendarView_firstDayOfWeek, Calendar.SUNDAY);
        mHourHeight = array.getDimensionPixelSize(R.styleable.CalendarView_hourHeight, UnitUtils.dip2px(context,50));
        array.recycle();
        mcontext=context;
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void  init(Context context) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.schedule_view, this, true);
        viewPager = view.findViewById(R.id.sv_viewpager);

        adapter=new SViewPagerAdapter(mViewType);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(VIEW_PAGER_START_POSITION, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                List<Date> list = adapter.currentShowDays(position);
                if (!list.isEmpty()){
                    pageChangedListener.changed(list);
                }else{
                    Log.i("CalendarView", "OnSchedulerPageChangedListener changed , showDays list is empty , maybe CalendarView is not show");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class SViewPagerAdapter extends PagerAdapter{
        private ArrayDeque<ScheduleView> currentViews=new  ArrayDeque<ScheduleView>() ;{
            currentViews.iterator();
        }
        private ArrayList<CalendarViewEvent> mEventList=new  ArrayList<CalendarViewEvent>() ;
        private SparseArray<List<Date>> mShowDays=new  SparseArray<List<Date>>() ;

        private  int viewType;
        public SViewPagerAdapter(int mViewType) {
            this.viewType=mViewType;
        }


        public void  setEventList(ArrayList<CalendarViewEvent> eventList) {
            mEventList.clear();
            mEventList.addAll(eventList);
            currentViews.forEach((view) ->
                    view.setEvents(eventList)
            );

        }

        public  List<Date>  currentShowDays( int position){
            try {
                if(mShowDays.get(position)==null){
                    return  new ArrayList<Date>();
                }
                 return  mShowDays.get(position);
            } catch (Exception e ){
                 return new ArrayList<Date>();
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = getItemView(container, position);
            ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, param);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {


            currentViews.remove((ScheduleView)object);
            mShowDays.remove(position);
            container.removeView((View) object);
        }

        private View getItemView(ViewGroup container,int position) {
            int mPosition = calBizPosition(position);
            ScheduleView view = new ScheduleView(
                    container.getContext(),
                    viewType,
                    mHeaderTextSize,
                    mHeaderColumnTextColor,
                    mTodayHeaderTextColor,
                    mTimeTextSize,
                    mTimeColumnTextColor,
                    mEventTextSize,
                    mHourHeight,
                    mFirstDayOfWeek
            );
            view.initDrawThing();//一定要执行
            //view.tag = mPosition;
            view.setTag(mPosition);

            view.setOnEventClickListener(new OnEventClickListener(){
                @Override
                public void eventClick(CalendarViewEvent event ){
                    eventClickListener.eventClick(event);
                }
            });
            view.setOnEventLongPressListener(new OnEventLongPressListener() {
                @Override
                public void eventLongPress(CalendarViewEvent event) {
                    eventLongPressListener.eventLongPress(event);
                }
            });
            mShowDays.put(position, view.setScheduleViewPagerPosition(mPosition));
            currentViews.add(view);
            view.setEvents(Constant.eventList);
            return view;
        }

        //计算偏移量，默认是减500
        private  int calBizPosition(int position) {
            return position - VIEW_PAGER_START_POSITION;
        }

        @Override
        public int getCount() {
            return 1000;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }
    }
    /**************
     * public
     */

    public void backToToday(){
        viewPager.setCurrentItem(VIEW_PAGER_START_POSITION, false);
    }
    public void addEvent(CalendarViewEvent event) {
        mEventList.add(event);
        adapter.setEventList(mEventList);
    }

    public void  addEvents( List<CalendarViewEvent> events) {
        mEventList.addAll(events);
        adapter.setEventList(mEventList);
    }

    public void  setEvents( List<CalendarViewEvent> events) {
        Log.i("CalenderView",events.toString());
        mEventList.clear();
        mEventList.addAll(events);
        adapter.setEventList(mEventList);
    }


    public void  removeEvents() {
        mEventList.clear();
        adapter.setEventList(mEventList);
    }


    public void setOnEventClickListener(OnEventClickListener listener) {
        eventClickListener = listener;
    }

    public void setOnSchedulerPageChangedListener(OnSchedulerPageChangedListener listener) {
        pageChangedListener = listener;
    }

    public void setOnEventLongPressListener(OnEventLongPressListener listener) {
        eventLongPressListener = listener;
    }



    // Get方法发送数据
    private void sendRequest(String start_time,String end_time) {
        String url = Constant.RMOTE_SERVER+"?start_date="+start_time+"&end_date="+end_time;
//      SharedPreferences sp = .getSharedPreferences("loginSera", MODE_PRIVATE);
        String token="";
        OkhttpHelper.getRequest(url,token ,new Callback() {
            @Override
            public void onFailure(Call call, IOException e){
                Log.i("CalendarView","onFailure:"+e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String result = response.body().string();
                if(response.isSuccessful())
                {
                    //回调的方法执行在子线程
                    List<CalendarViewEvent> events = parseJSONWithGSON(result);
                    setEvents(events);
                    Log.d("result:",result);
                }
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    //url = url + "?start_date=2023-7-"+day+"&end_date=2023-7-"+day;
//                    url = url + "?start_date=2023-7-10&end_date=2023-7-10";
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .header("Authorization", "Bearer " + token)
//                            .build();
//                    client.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                            e.printStackTrace();
//                        }
//                        @Override
//                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                            if (response.isSuccessful()) {
//                                lineChart = rootView.findViewById(R.id.lineChart);
//                                String statue = "";
//                                final String responseData = response.body().string();
//                                float start_time = 0;
//                                float end_time = 0;
//                                JSONArray jsonArray = null;
//                                try {
//                                    jsonArray = new JSONArray(responseData);
//                                } catch (JSONException e) {
//                                    throw new RuntimeException(e);
//                                }
//                                // 使用Activity的runOnUiThread方法切回主线程
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        // 在这里更新Fragment的UI
//
//                                    }
//                                });
//                            }
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }


    //解析json字符串
    private  List<CalendarViewEvent> parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        List<DayActivityData> actList = gson.fromJson(jsonData,new TypeToken<List<DayActivityData>>(){}.getType());
        List<CalendarViewEvent> events=new ArrayList<>();
        for(DayActivityData dayActivityData:actList){
            CalendarViewEvent event=new CalendarViewEvent(dayActivityData.getStart_time(),
                    dayActivityData.getEnd_time(),dayActivityData.getState());
            events.add(event);
        }
        return events;
    }


}
