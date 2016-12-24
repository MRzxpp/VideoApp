package com.haishanda.android.videoapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.MonitorConfigBean;
import com.haishanda.android.videoapp.Bean.TimeBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Fragement.MonitorTimeFragment;
import com.haishanda.android.videoapp.Fragement.MonitorWarningFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.haishanda.android.videoapp.greendao.gen.TimeBeanDao;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

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
    @BindView(R.id.boats_config_messages)
    ListView boatsConfig;

    private final String Tag = "MonitorConfig";
    private int machineId;
    private long[] machineIds;
    private String[] boatNames;
    private int[] isSwitchOns;

    private String[] times = new String[4];

    MonitorConfigBeanDao monitorConfigBeanDao;

    public MonitorConfigActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_config);
        ButterKnife.bind(this);
    }

    private void initAdapterDatas() {
        monitorConfigBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorConfigBeanDao();
        QueryBuilder<MonitorConfigBean> queryBuilder = monitorConfigBeanDao.queryBuilder();
        List<MonitorConfigBean> monitorConfigBeanList = queryBuilder.list();
        machineIds = new long[monitorConfigBeanList.size()];
        boatNames = new String[monitorConfigBeanList.size()];
        isSwitchOns = new int[monitorConfigBeanList.size()];
        for (int i = 0; i < monitorConfigBeanList.size(); i++) {
            MonitorConfigBean m = monitorConfigBeanList.get(i);
            machineIds[i] = m.getMachineId();
            boatNames[i] = m.getBoatName();
            isSwitchOns[i] = m.getIsSwitchOn();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAdapterDatas();
        boatsConfig.setAdapter(new MonitorConfigAdapter(this, boatNames, machineIds, isSwitchOns));
    }

    private void setTimeDisplay(TextView timeDisplay, long machineId) {
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
        if (times[1].equals("0-1") && times[3].equals("0-2")) {
            timeDisplay.setText("尚未设置");
        } else {
            timeDisplay.setText(((times[0] != null) ? times[0] : "12") + ":"
                    + ((times[1] != null) ? times[1] : "30") + "~" + ((times[2] != null) ? times[2] : "12")
                    + ":" + ((times[3] != null) ? times[3] : "30"));
        }
    }

    @OnClick(R.id.exit_monitor_config)
    public void backToLastPage() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.monitor_warning)
    public void skipToMonitorWarningFragment(View view) {
        MonitorWarningFragment monitorWarningFragment = new MonitorWarningFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.monitor_config_layout, monitorWarningFragment);
        fragmentTransaction.commit();
    }

    public void refresh() {
        onResume();
    }

    public class MonitorConfigAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;
        private String[] boatNames;
        private long[] machineIds;
        private int[] isSwitchOpens;

        MonitorConfigAdapter(Context context, String[] boatNames, long[] machineIds, int isSwitchOpens[]) {
            super(context, R.layout.adapter_monitor_config, boatNames);
            this.context = context;
            this.boatNames = boatNames;
            this.machineIds = machineIds;
            this.isSwitchOpens = isSwitchOpens;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.adapter_monitor_config, parent, false);
            }
            RelativeLayout monitorTime = (RelativeLayout) convertView.findViewById(R.id.monitor_time);
            SwitchButton monitorOpen = (SwitchButton) convertView.findViewById(R.id.is_monitor_open);
            RelativeLayout monitorVoice = (RelativeLayout) convertView.findViewById(R.id.monitor_voice);
            TextView monitorBoatname = (TextView) convertView.findViewById(R.id.monitor_boatname);
            TextView monitorTimeDisplay = (TextView) convertView.findViewById(R.id.monitor_time_display);
            TextView monitorVoiceDisplay = (TextView) convertView.findViewById(R.id.monitor_voice_display);
            setTimeDisplay(monitorTimeDisplay, machineIds[position]);
            monitorBoatname.setText(boatNames[position]);
            monitorTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle data = new Bundle();
                    data.putLong("machineId", machineIds[position]);
                    MonitorTimeFragment monitorTimeFragment = new MonitorTimeFragment();
                    monitorTimeFragment.setArguments(data);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
                    fragmentTransaction.replace(R.id.monitor_config_layout, monitorTimeFragment);
                    fragmentTransaction.commit();
                }
            });
            if (isSwitchOpens[position] == 1) {
                monitorOpen.setChecked(true);
            } else {
                monitorOpen.setChecked(false);
            }
            monitorOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isSwitchOpens[position] = 1;
                        ApiManage.getInstence().getMonitorApiService().editMonitorSwitch(machineIds[position], true)
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
                                            QueryBuilder<MonitorConfigBean> queryBuilder = monitorConfigBeanDao.queryBuilder();
                                            MonitorConfigBean monitorConfigBean = queryBuilder.where(MonitorConfigBeanDao.Properties.MachineId.eq(machineIds[position])).unique();
                                            monitorConfigBean.setIsSwitchOn(1);
                                            monitorConfigBeanDao.update(monitorConfigBean);
                                        } else {
                                            Toast.makeText(MonitorConfigActivity.this, smartResult.getMsg() != null ? smartResult.getMsg() : "开启失败", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        Log.i(Tag, "isChecked");
                    } else {
                        isSwitchOpens[position] = 0;
                        ApiManage.getInstence().getMonitorApiService().editMonitorSwitch(machineIds[position], false)
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
                                            QueryBuilder<MonitorConfigBean> queryBuilder = monitorConfigBeanDao.queryBuilder();
                                            MonitorConfigBean monitorConfigBean = queryBuilder.where(MonitorConfigBeanDao.Properties.MachineId.eq(machineIds[position])).unique();
                                            monitorConfigBean.setIsSwitchOn(0);
                                            monitorConfigBeanDao.update(monitorConfigBean);
                                        } else {
                                            Toast.makeText(MonitorConfigActivity.this, smartResult.getMsg() != null ? smartResult.getMsg() : "关闭失败", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        Log.i(Tag, "isNotChecked");
                    }
                }
            });

            monitorVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
        }
    }

}
