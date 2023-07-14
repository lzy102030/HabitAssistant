package com.example.habitassistant.ScheduleMgrWidgt.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.habitassistant.ScheduleMgrWidgt.fragment.ScheduleWeekFragment;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends FragmentStateAdapter {
    ArrayList<ScheduleWeekFragment> fragments;
    private List<String> mData = new ArrayList<>();

    public ScheduleAdapter(@NonNull FragmentActivity fragmentActivity, List<String> mData, ArrayList<ScheduleWeekFragment>fragments) {
        super(fragmentActivity);
        this.mData = mData;
        this.fragments=fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //TODO 待修正
        if(position==0){
            return com.example.habitassistant.ScheduleMgrWidgt.ScheduleWeekFragment.newInstance();
        }else if (position==1){
            return com.example.habitassistant.ScheduleMgrWidgt.ScheduleMonthFragment.newInstance();
        } else if (position==2) {
            return com.example.habitassistant.ScheduleMgrWidgt.ScheduleWeekFragment.newInstance();
        } else if (position==3) {
            return com.example.habitassistant.ScheduleMgrWidgt.ScheduleDayFragment.newInstance();
        }else {
            return com.example.habitassistant.ScheduleMgrWidgt.ScheduleAgendaFragment.newInstance();
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
