package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.MonitorConfigActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.TimeBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.Views.PickerView;
import com.haishanda.android.videoapp.greendao.gen.TimeBeanDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改船舶时间
 * Created by Zhongsz on 2016/11/1.
 */

public class MonitorTimeFragment extends Fragment {
    @BindView(R.id.starttime_hour)
    PickerView startTimeHour;
    @BindView(R.id.starttime_minute)
    PickerView startTimeMinute;
    @BindView(R.id.endtime_hour)
    PickerView endTimeHour;
    @BindView(R.id.endtime_minute)
    PickerView endTimeMinute;

    String[] times = new String[4];
    int begin = 0;
    int endPerform = 0;

    private long machineId;
    private final String TAG = "保存监控时间";
    private TimeBean timeBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_time, container, false);
        ButterKnife.bind(this, view);
        Bundle data = getArguments();
        machineId = data.getLong("machineId");
        initTimePicker();
        return view;
    }

    @OnClick(R.id.back_to_monitor_config_btn)
    public void backToLastPage() {
        MonitorConfigActivity monitorConfigActivity = (MonitorConfigActivity) getActivity();
        monitorConfigActivity.refresh();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    public void initTimePicker() {
        TimeBeanDao timeBeanDao = VideoApplication.getApplication().getDaoSession().getTimeBeanDao();
        QueryBuilder<TimeBean> queryBuilder = timeBeanDao.queryBuilder();
        List<String> hour = new ArrayList<>();
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hour.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i < 10 ? "0" + i : "" + i);
        }
        startTimeHour.setData(hour);
        startTimeMinute.setData(minutes);
        endTimeHour.setData(hour);
        endTimeMinute.setData(minutes);
        try {
            timeBean = queryBuilder.where(TimeBeanDao.Properties.MachineId.eq(machineId)).uniqueOrThrow() != null ?
                    queryBuilder.where(TimeBeanDao.Properties.MachineId.eq(machineId)).uniqueOrThrow() : new TimeBean(12, 30, 12, 30, machineId);
            if (timeBean.getBeginMinute() == -1 && timeBean.getEndMinute() == -2) {
                timeBean = new TimeBean(12, 30, 12, 30, machineId);
            }
            begin = 60 * timeBean.getBeginHour() + timeBean.getBeginMinute();
            endPerform = 60 * timeBean.getEndHour() + timeBean.getEndMinute();
            times = new String[]{String.valueOf(timeBean.getBeginHour()),
                    String.valueOf(timeBean.getBeginMinute()),
                    String.valueOf(timeBean.getEndHour()),
                    String.valueOf(timeBean.getEndMinute())};
        } catch (DaoException e) {
            e.printStackTrace();
            timeBean = new TimeBean(12, 30, 12, 30, machineId);
            begin = 60 * timeBean.getBeginHour() + timeBean.getBeginMinute();
            endPerform = 60 * timeBean.getEndHour() + timeBean.getEndMinute();
            times = new String[]{String.valueOf(timeBean.getBeginHour()),
                    String.valueOf(timeBean.getBeginMinute()),
                    String.valueOf(timeBean.getEndHour()),
                    String.valueOf(timeBean.getEndMinute())};
        }
        startTimeHour.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                times[0] = text;
                begin = begin - 60 * timeBean.getBeginHour() + 60 * Integer.valueOf(text);
            }
        });
        startTimeMinute.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                times[1] = text;
                begin = begin - timeBean.getBeginMinute() + Integer.valueOf(text);

            }
        });
        endTimeHour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                times[2] = text;
                endPerform = endPerform - 60 * timeBean.getEndHour() + 60 * Integer.valueOf(text);

            }
        });
        endTimeMinute.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                times[3] = text;
                endPerform = endPerform - timeBean.getEndHour() + Integer.valueOf(text);

            }
        });
        startTimeHour.setSelected(timeBean.getBeginHour());
        startTimeMinute.setSelected(timeBean.getBeginMinute());
        endTimeHour.setSelected(timeBean.getEndHour());
        endTimeMinute.setSelected(timeBean.getEndMinute());
    }


    @OnClick(R.id.save_monitor_time_btn)
    public void saveMonitorTime() {
        int beginHour = Integer.valueOf(times[0]);
        int beginMinute = Integer.valueOf(times[1]);
        int endHour = Integer.valueOf(times[2]);
        int endMinute = Integer.valueOf(times[3]);
        int begin = 60 * beginHour + beginMinute;
        int span = 0;
        if (beginHour < endHour) {
            span = (60 * endHour + endMinute) - begin;
        } else if (beginHour > endHour) {
            span = (60 * (endHour + 24) + endMinute) - begin;
        } else if (beginHour == endHour) {
            if (beginMinute < endMinute) {
                span = (60 * endHour + endMinute) - begin;
            } else if (beginMinute > endMinute) {
                span = (60 * (endHour + 24) + endMinute) - begin;
            } else if (beginMinute == endMinute) {
                span = 0;
            }
        }
        ApiManage.getInstence().getMonitorApiService().editMonitorTime(machineId, span, begin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "save completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "save error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            backToLastPage();
                            Log.i(TAG, "succeed");
                        } else {
                            Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "时间段保存失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        Log.i("picker", times[1]);
        TimeBeanDao timeBeanDao = VideoApplication.getApplication().getDaoSession().getTimeBeanDao();
        TimeBean timeBean = new TimeBean(beginHour, beginMinute, endHour, endMinute, machineId);
        timeBeanDao.insertOrReplace(timeBean);
        MonitorConfigActivity monitorConfigActivity = (MonitorConfigActivity) getActivity();
        monitorConfigActivity.refresh();
    }
}
