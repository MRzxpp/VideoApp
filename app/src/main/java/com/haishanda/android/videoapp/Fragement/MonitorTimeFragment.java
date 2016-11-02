package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Views.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.Log;

/**
 * Created by Zhongsz on 2016/11/1.
 */

public class MonitorTimeFragment extends Fragment {
    @BindView(R.id.next_day)
    TextView nextDay;
    @BindView(R.id.starttime_hour)
    PickerView startTimeHour;
    @BindView(R.id.starttime_minute)
    PickerView startTimeMinute;
    @BindView(R.id.endtime_hour)
    PickerView endTimeHour;
    @BindView(R.id.endtime_minute)
    PickerView endTimeMinute;
    String[] times = new String[4];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_time, container, false);
        ButterKnife.bind(this, view);
        initTimePicker();
        return view;
    }

    public void initTimePicker() {
        List<String> hour = new ArrayList<String>();
        List<String> minutes = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            hour.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i < 10 ? "0" + i : "" + i);
        }
        startTimeHour.setData(hour);
        startTimeHour.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                times[0] = text;
                Toast.makeText(getContext(), "选择了 " + text + " 时",
                        Toast.LENGTH_SHORT).show();
            }
        });
        startTimeMinute.setData(minutes);
        startTimeMinute.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                times[1] = text;
                Toast.makeText(getContext(), "选择了 " + text + " 分",
                        Toast.LENGTH_SHORT).show();
            }
        });
        endTimeHour.setData(hour);
        endTimeHour.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                times[2] = text;
                Toast.makeText(getContext(), "选择了 " + text + " 时",
                        Toast.LENGTH_SHORT).show();
            }
        });
        endTimeMinute.setData(minutes);
        endTimeMinute.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                times[3] = text;
                Toast.makeText(getContext(), "选择了 " + text + " 分",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.save_monitor_time_btn)
    public void saveMonitorTime() {
        Log.i("picker", times[1]);
    }
}
