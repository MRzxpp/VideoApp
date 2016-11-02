package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.haishanda.android.videoapp.Fragement.AboutBoatFragment;
import com.haishanda.android.videoapp.Fragement.MonitorTimeFragment;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/1.
 */

public class MonitorConfigActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_config);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.monitor_time)
    public void skipToMonitorTimeFragment(View view) {
        MonitorTimeFragment monitorTimeFragment = new MonitorTimeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.monitor_config_layout, monitorTimeFragment);
        fragmentTransaction.commit();
    }
}
