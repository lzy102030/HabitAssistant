package com.example.habitassistant.ScheduleMgrWidgt.fragment;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.habitassistant.R;
import com.example.habitassistant.ScheduleMgrWidgt.library.WeekView.CalendarView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleWeekFragment  extends Fragment {
    Context mContext;

    String title;
    String content;

    private  static  final List<Integer> colors=new ArrayList<>();
    {
        colors.add(R.color.blue);
        colors.add(R.color.black);
        colors.add(R.color.purple);
    }
    private final int position;


    public ScheduleWeekFragment(String title,String content,int position) {
        this.title=title;
        this.content=content;
        this.position=position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.activity_schedule_week, container, false);
        CalendarView schedule_week = view.findViewById(R.id.schedule_week);

        return view;
    }
}
