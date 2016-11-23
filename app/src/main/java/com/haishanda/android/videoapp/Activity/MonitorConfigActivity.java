package com.haishanda.android.videoapp.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Fragement.MonitorTimeFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/11/1.
 */

public class MonitorConfigActivity extends FragmentActivity {
    @BindView(R.id.is_monitor_open)
    SwitchButton isMonitorOpen;

    private final String Tag = "MonitorConfig";
    private int machineId;
    private boolean isSwitchOpen;

    private final String[] array = {"Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_config);
        ButterKnife.bind(this);
        machineId = VideoApplication.getApplication().getCurrentMachineId();
        setOnCheckedListener();
    }

    @OnClick(R.id.exit_monitor_config)
    public void backToLastPage() {
        this.finish();
    }

    @OnClick(R.id.monitor_time)
    public void skipToMonitorTimeFragment(View view) {
        MonitorTimeFragment monitorTimeFragment = new MonitorTimeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.monitor_config_layout, monitorTimeFragment);
        fragmentTransaction.commit();
    }

    public void setOnCheckedListener() {
        isMonitorOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSwitchOpen = true;
                    ApiManage.getInstence().getMonitorApiService().editMonitorSwitch(machineId, isSwitchOpen)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<SmartResult>() {
                                @Override
                                public void onCompleted() {
                                    Log.i(Tag, "连接完成");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.i(Tag, "连接错误");
                                }

                                @Override
                                public void onNext(SmartResult smartResult) {
                                    if (smartResult.getCode() == 1) {
                                        Toast.makeText(MonitorConfigActivity.this, "监控功能已开启", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MonitorConfigActivity.this, smartResult.getMsg() != null ? smartResult.getMsg() : "开启失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    Log.i(Tag, "isChecked");
                } else {
                    isSwitchOpen = false;
                    ApiManage.getInstence().getMonitorApiService().editMonitorSwitch(machineId, isSwitchOpen)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<SmartResult>() {
                                @Override
                                public void onCompleted() {
                                    Log.i(Tag, "连接完成");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.i(Tag, "连接错误");
                                }

                                @Override
                                public void onNext(SmartResult smartResult) {
                                    if (smartResult.getCode() == 1) {
                                        Toast.makeText(MonitorConfigActivity.this, "监控功能已关闭", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MonitorConfigActivity.this, smartResult.getMsg() != null ? smartResult.getMsg() : "关闭失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    Log.i(Tag, "isNotChecked");
                }
            }
        });
    }
}
