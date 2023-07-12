package com.example.habitassistant.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.habitassistant.fragment.MonthFragment;
import com.example.habitassistant.fragment.DayFragment;
import com.example.habitassistant.fragment.WeekFragment;

import java.util.ArrayList;
import java.util.List;

public class StatisticsAdapter extends FragmentStateAdapter {
    private List<String> mData = new ArrayList<>();

    public StatisticsAdapter(@NonNull FragmentActivity fragmentActivity, List<String> data) {
        super(fragmentActivity);
        mData = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return DayFragment.newInstance();
        } else if (position==1) {
            return WeekFragment.newInstance();
        }else {
            return MonthFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
