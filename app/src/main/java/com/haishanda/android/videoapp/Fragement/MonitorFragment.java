package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haishanda.android.videoapp.Activity.AddBoatActivity;
import com.haishanda.android.videoapp.Activity.MonitorConfigActivity;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/14.
 */

public class MonitorFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.monitor_config)
    public void skipToMonitorConfigFragment(View view) {
        Intent intent = new Intent(getActivity(), MonitorConfigActivity.class);
        startActivity(intent);
    }
}

