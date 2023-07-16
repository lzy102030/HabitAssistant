package com.example.habitassistant.ScheduleMgrWidgt;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.habitassistant.R;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.CalendarView;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.CalendarViewEvent;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnEventClickListener;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnEventLongPressListener;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.OnSchedulerPageChangedListener;
import com.example.habitassistant.entity.DayActivityData;
import com.example.habitassistant.utils.ChartHelper;
import com.example.habitassistant.utils.Constant;
import com.example.habitassistant.utils.OkhttpHelper;
import com.example.habitassistant.utils.TimeConverter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleWeekFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private int nums=0;
    private String mParam1;

    private OnEventClickListener clickListener;
    private OnEventLongPressListener longclickListener;
    private ArrayList<CalendarViewEvent> eventList = new ArrayList<CalendarViewEvent>();
    private CalendarView schedule_week;
    private View rootView;

    public ScheduleWeekFragment() {
        // Required empty public constructor

    }

    public static ScheduleWeekFragment newInstance() {
        ScheduleWeekFragment fragment = new ScheduleWeekFragment();
        Bundle args = new Bundle();
        ArrayList<CalendarViewEvent> eventList1 = Constant.eventList;
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
        rootView = inflater.inflate(R.layout.activity_schedule_week, container, false);
        schedule_week = rootView.findViewById(R.id.schedule_week);

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

        String url = Constant.RMOTE_SERVER+"?start_date="+"2022-10-16"+"&end_date="+"2024-12-16";
        SharedPreferences sp = getActivity().getSharedPreferences("loginSera", MODE_PRIVATE);
        String token = sp.getString("token", "");
        //渲染数据
//        sendRequest("2002-10-16","2024-10-16");
        schedule_week.addEvents(Constant.eventList);

//        eventList.addAll(obtainEvents());
//        schedule_week.setEvents(eventList);


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
    // Get方法发送数据
    private  void sendRequest(String start_time, String end_time) {
        String url = Constant.RMOTE_SERVER+"?start_date="+start_time+"&end_date="+end_time;
        SharedPreferences sp = getActivity().getSharedPreferences("loginSera", MODE_PRIVATE);
        String token = sp.getString("token", "");
        OkhttpHelper.getRequest(url,token ,new Callback() {
            @Override
            public void onFailure(Call call, IOException e){
                Log.i("CalendarView","onFailure:"+e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String result = response.body().string();
                Log.i("Schedule",result);
                if(response.isSuccessful())
                {
                    //回调的方法执行在子线程
                    eventList.clear();
                    List<CalendarViewEvent> events = parseJSONWithGSON(result);
                    eventList.addAll(events);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            schedule_week.setEvents(eventList);

                        }
                    });
                    Log.d("result:",result);
                }
            }
        });
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