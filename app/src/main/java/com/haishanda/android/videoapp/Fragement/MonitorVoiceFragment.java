package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.haishanda.android.videoapp.Activity.MonitorConfigActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改船上报警音
 * Created by Zhongsz on 2017/2/8.
 */

//Todo 服务器传来的船舶信息中应当加入voice字段

public class MonitorVoiceFragment extends Fragment {
    int selectedVoiceId;
    long machineId;
    private static final String TAG = "MonitorVoiceFragment";

    @BindView(R.id.voice_group)
    RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_voice, container, false);
        ButterKnife.bind(this, view);
        Bundle data = getArguments();
        machineId = data.getLong("machineId");
        selectedVoiceId = data.getInt("selectedVoice");
        initRadioButton(selectedVoiceId);
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

    private void initRadioButton(int selectedVoiceId) {
        radioGroup.check(selectedVoiceId);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int voice;
                if (checkedId == R.id.no_voice) {
                    voice = 1;
                } else if (checkedId == R.id.default_voice) {
                    voice = 2;
                } else if (checkedId == R.id.custom_voice) {
                    voice = 3;
                } else {
                    voice = 0;
                }
                chooseAlarmVoice(machineId, voice);
            }
        });
    }

    private void chooseAlarmVoice(long machineId, int voice) {
        ApiManage.getInstence().getMonitorApiService().chooseAlarmVoice(machineId, voice)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "edit completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "edit error-------" + e.toString());
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            Log.d(TAG, "edit success");
                        } else {
                            Log.d(TAG, smartResult.getMsg() != null ? smartResult.getMsg() : "edit failed!!!");
                        }
                    }
                });
    }
}
