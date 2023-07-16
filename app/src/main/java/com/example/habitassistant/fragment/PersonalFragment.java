package com.example.habitassistant.fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.habitassistant.MainActivity;
import com.example.habitassistant.R;

public class PersonalFragment extends Fragment {
    private MainActivity mainActivity;

    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private Switch switch4;
    private Button button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);

        switch1 = rootView.findViewById(R.id.switch1);
        switch2 = rootView.findViewById(R.id.switch2);
        switch3 = rootView.findViewById(R.id.switch3);
        switch4 = rootView.findViewById(R.id.switch4);
        button = rootView.findViewById(R.id.button);

        mainActivity = new MainActivity();

        switch1.setChecked(mainActivity.getJiaoxuelou_state());
        switch2.setChecked(mainActivity.getTushuguan_state());
        switch3.setChecked(mainActivity.getXiuxi_state());
        switch4.setChecked(mainActivity.getTushuguanyuding_state());


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 处理 Switch 状态改变的逻辑
                mainActivity.setJiaoxuelou_state(switch1.isChecked());
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 处理 Switch 状态改变的逻辑
                mainActivity.setTushuguan_state(switch2.isChecked());
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 处理 Switch 状态改变的逻辑
                mainActivity.setXiuxi_state(switch3.isChecked());
            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 处理 Switch 状态改变的逻辑
                mainActivity.setTushuguanyuding_state(switch4.isChecked());
            }
        });

        return rootView;
    }

    public void exitButton(View view) {
        //退出操作
    }
}