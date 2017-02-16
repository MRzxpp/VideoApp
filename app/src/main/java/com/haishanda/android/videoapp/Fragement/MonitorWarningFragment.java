package com.haishanda.android.videoapp.Fragement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.Constant;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ExpandableLayout;
import com.haishanda.android.videoapp.Views.PickerView;
import com.kyleduo.switchbutton.SwitchButton;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * 手机提醒功能的设置
 * Created by Zhongsz on 2016/11/23.
 */

public class MonitorWarningFragment extends Fragment {
    @BindView(R.id.is_notification_open)
    SwitchButton isNotificationOpen;
    @BindView(R.id.notification_open)
    ExpandableLayout notificationOpenLayout;
    @BindView(R.id.is_mobile_shake_open)
    SwitchButton isShakeOpen;
    @BindView(R.id.is_mobile_voice_open)
    SwitchButton isVoiceOpen;
    @BindView(R.id.is_sending_email)
    SwitchButton isSendEmail;
    @BindView(R.id.time_gap)
    TextView timeGap;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_warning, container, false);
        ButterKnife.bind(this, view);
        sharedPreferences = getActivity().getSharedPreferences(Constant.WARNING_CONFIG, MODE_PRIVATE);
        timeGap.setText(String.valueOf(sharedPreferences.getInt(Constant.WARNING_CONFIG_TIME_SPAN, 15)) + "分钟");
        setIsNotificationOpenListener();
        setIsEmailSendListener();
        setIsShakeOpenListener();
        setIsVoiceOpenListener();
        initSwitchButtons();
        return view;
    }

    @OnClick(R.id.monitor_time_span)
    public void setMonitorTimeSpan() {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initSwitchButtons() {
        if (sharedPreferences.getBoolean(Constant.WARNING_CONFIG_VIBRATE, true)) {
            isShakeOpen.setChecked(true);
        } else {
            isShakeOpen.setChecked(false);
        }
        if (sharedPreferences.getBoolean(Constant.WARNING_CONFIG_VOICE, true)) {
            isVoiceOpen.setChecked(true);
        } else {
            isVoiceOpen.setChecked(false);
        }
        if (sharedPreferences.getBoolean(Constant.WARNING_CONFIG_TIMER, true)) {
            isNotificationOpen.setChecked(true);
            if (!notificationOpenLayout.isOpened()) {
                notificationOpenLayout.show();
            }
        } else {
            isNotificationOpen.setChecked(false);
            if (notificationOpenLayout.isOpened()) {
                notificationOpenLayout.hide();
            }
        }
        if (sharedPreferences.getBoolean(Constant.WARNING_CONFIG_SMS, true)) {
            isSendEmail.setChecked(true);
        } else {
            isSendEmail.setChecked(false);
        }
    }

    private void setIsShakeOpenListener() {
        isShakeOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean(Constant.WARNING_CONFIG_VIBRATE, true);
                    editor.apply();
                } else {
                    editor.putBoolean(Constant.WARNING_CONFIG_VIBRATE, false);
                    editor.apply();
                }
            }
        });
    }

    private void setIsVoiceOpenListener() {
        isVoiceOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean(Constant.WARNING_CONFIG_VOICE, true);
                    editor.apply();
                } else {
                    editor.putBoolean(Constant.WARNING_CONFIG_VOICE, false);
                    editor.apply();
                }
            }
        });
    }

    private void setIsNotificationOpenListener() {
        isNotificationOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    if (!notificationOpenLayout.isOpened()) {
                        notificationOpenLayout.show();
                    }
                    editor.putBoolean(Constant.WARNING_CONFIG_TIMER, true);
                    editor.apply();
                } else {
                    if (notificationOpenLayout.isOpened()) {
                        notificationOpenLayout.hide();
                    }
                    editor.putBoolean(Constant.WARNING_CONFIG_TIMER, false);
                    editor.apply();
                }
            }
        });
    }

    private void setIsEmailSendListener() {
        isSendEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ApiManage.getInstence().getMonitorApiService().editMonitorSms(true)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<SmartResult>() {
                                @Override
                                public void onCompleted() {
                                    Log.d("是否接受短信", "completed");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("是否接受短信", "error");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(SmartResult smartResult) {
                                    if (smartResult.getCode() == 1) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(Constant.WARNING_CONFIG_SMS, true);
                                        editor.apply();
                                        Log.d("是否接受短信", "yes");
                                    } else {
                                        Log.d("是否接受短信", "failed");
                                        Toast.makeText(getContext(), "修改失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    ApiManage.getInstence().getMonitorApiService().editMonitorSms(false)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<SmartResult>() {
                                @Override
                                public void onCompleted() {
                                    Log.d("是否接受短信", "completed");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("是否接受短信", "error");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(SmartResult smartResult) {
                                    if (smartResult.getCode() == 1) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(Constant.WARNING_CONFIG_SMS, false);
                                        editor.apply();
                                        Log.d("是否接受短信", "no");
                                    } else {
                                        Log.d("是否接受短信", "failed");
                                        Toast.makeText(getContext(), "修改失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @OnClick(R.id.back_to_monitor_config_btn)
    public void backToMonitorConfigPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.voice_gap)
    public void setVoiceGap() {
        ViewHolder viewHolder = new ViewHolder(R.layout.adapter_voice_gap);

        DialogPlus dialogPlus = DialogPlus.newDialog(getContext())
                .setContentHolder(viewHolder)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setExpanded(false)
                .create();

        View view = viewHolder.getInflatedView();
        PickerView voiceGapPicker = (PickerView) view.findViewById(R.id.voice_gap_picker);
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            minutes.add(i < 10 ? "0" + i : "" + i);
        }
        voiceGapPicker.setData(minutes);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.WARNING_CONFIG, MODE_PRIVATE);
        voiceGapPicker.setSelected(sharedPreferences.getInt(Constant.WARNING_CONFIG_TIME_SPAN, 15));
        voiceGapPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.WARNING_CONFIG, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constant.WARNING_CONFIG_TIME_SPAN, Integer.valueOf(text));
                editor.apply();
                timeGap.setText(text + "分钟");
                Log.d("监控报警时间间隔", text);
            }
        });
        dialogPlus.show();
    }


}
