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
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.TimeBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Fragement.MonitorTimeFragment;
import com.haishanda.android.videoapp.Fragement.MonitorWarningFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ExpandableLayout;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.TimeBeanDao;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

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
    @BindView(R.id.monitor_time_display)
    TextView timeDisplay;

    private final String Tag = "MonitorConfig";
    private int machineId;
    private boolean isSwitchOpen;

    private String[] times = new String[4];

    private final String[] array = {"Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_config);
        ButterKnife.bind(this);
        machineId = VideoApplication.getApplication().getCurrentMachineId();
        setOnCheckedListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimeBeanDao timeBeanDao = VideoApplication.getApplication().getDaoSession().getTimeBeanDao();
        QueryBuilder<TimeBean> queryBuilder = timeBeanDao.queryBuilder();
        try {
            TimeBean timeBean = queryBuilder.where(TimeBeanDao.Properties.MachineId.eq(machineId)).uniqueOrThrow() != null
                    ? queryBuilder.where(TimeBeanDao.Properties.MachineId.eq(machineId)).uniqueOrThrow() : new TimeBean(12, 30, 12, 30, machineId);
            times[0] = timeBean.getBeginHour() >= 10 ? String.valueOf(timeBean.getBeginHour()) : ("0" + String.valueOf(timeBean.getBeginHour()));
            times[1] = timeBean.getBeginMinute() >= 10 ? String.valueOf(timeBean.getBeginMinute()) : ("0" + String.valueOf(timeBean.getBeginMinute()));
            times[2] = timeBean.getEndHour() >= 10 ? String.valueOf(timeBean.getEndHour()) : ("0" + String.valueOf(timeBean.getEndHour()));
            times[3] = timeBean.getEndMinute() >= 10 ? String.valueOf(timeBean.getEndMinute()) : ("0" + String.valueOf(timeBean.getEndMinute()));
        } catch (DaoException e) {
            e.printStackTrace();
            TimeBean timeBean = new TimeBean(12, 30, 12, 30, machineId);
            times[0] = timeBean.getBeginHour() >= 10 ? String.valueOf(timeBean.getBeginHour()) : ("0" + String.valueOf(timeBean.getBeginHour()));
            times[1] = timeBean.getBeginMinute() >= 10 ? String.valueOf(timeBean.getBeginMinute()) : ("0" + String.valueOf(timeBean.getBeginMinute()));
            times[2] = timeBean.getEndHour() >= 10 ? String.valueOf(timeBean.getEndHour()) : ("0" + String.valueOf(timeBean.getEndHour()));
            times[3] = timeBean.getEndMinute() >= 10 ? String.valueOf(timeBean.getEndMinute()) : ("0" + String.valueOf(timeBean.getEndMinute()));
        }
        timeDisplay.setText(((times[0] != null) ? times[0] : "12") + ":"
                + ((times[1] != null) ? times[1] : "30") + "/" + ((times[2] != null) ? times[2] : "12")
                + ":" + ((times[3] != null) ? times[3] : "30"));
    }

    @OnClick(R.id.exit_monitor_config)
    public void backToLastPage() {
        this.finish();
    }

    @OnClick(R.id.monitor_time)
    public void skipToMonitorTimeFragment(View view) {
        Bundle data = new Bundle();
        data.putInt("machineId", machineId);
        MonitorTimeFragment monitorTimeFragment = new MonitorTimeFragment();
        monitorTimeFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.monitor_config_layout, monitorTimeFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.monitor_warning)
    public void skipToMonitorWarningFragment(View view) {
        MonitorWarningFragment monitorWarningFragment = new MonitorWarningFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.monitor_config_layout, monitorWarningFragment);
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

    public void refresh() {
        onResume();
    }
}
