package com.example.habitassistant.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class MyFragmentStateAdapter extends FragmentStateAdapter {

    private List<Fragment> mData;

    public MyFragmentStateAdapter(FragmentActivity fragmentActivity, List<Fragment> mData) {
        super(fragmentActivity);
        this.mData = mData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }
}