package com.haishanda.android.videoapp.Fragement;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.MonitorWarningBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ExpandableLayout;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.MonitorWarningBeanDao;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.VIBRATOR_SERVICE;

/**
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

    MonitorWarningBean monitorWarningBean;
    MonitorWarningBeanDao monitorWarningBeanDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_warning, container, false);
        ButterKnife.bind(this, view);
        monitorWarningBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorWarningBeanDao();
        QueryBuilder<MonitorWarningBean> queryBuilder = monitorWarningBeanDao.queryBuilder();
        try {
            monitorWarningBean = queryBuilder.unique();
            if (monitorWarningBean == null) {
                monitorWarningBean = new MonitorWarningBean(1, false, false, false, false);
                monitorWarningBeanDao.insertOrReplace(monitorWarningBean);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            monitorWarningBean = new MonitorWarningBean(1, false, false, false, false);
            monitorWarningBeanDao.insertOrReplace(monitorWarningBean);
        }
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
        monitorWarningBeanDao.update(monitorWarningBean);
    }

    private void initSwitchButtons() {
        if (monitorWarningBean.getIsShakeOpen()) {
            isShakeOpen.setChecked(true);
        } else {
            isShakeOpen.setChecked(false);
        }
        if (monitorWarningBean.getIsVoiceOpen()) {
            isVoiceOpen.setChecked(true);
        } else {
            isVoiceOpen.setChecked(false);
        }
        if (monitorWarningBean.getIsNotificationOpen()) {
            isNotificationOpen.setChecked(true);
        } else {
            isNotificationOpen.setChecked(false);
        }
        if (monitorWarningBean.getIsSendingEmail()) {
            isSendEmail.setChecked(true);
        } else {
            isSendEmail.setChecked(false);
        }
    }

    private void setIsShakeOpenListener() {
        if (monitorWarningBean.getIsVoiceOpen()) {
            isShakeOpen.setChecked(true);
        } else {
            isShakeOpen.setChecked(false);
        }
        isShakeOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {1000, 2000, 1000, 3000};
                if (isChecked) {
                    vibrator.vibrate(pattern, -1);
                    vibrator.cancel();
                    monitorWarningBean.setIsShakeOpen(true);
//                    monitorWarningBeanDao.update(monitorWarningBean);
                } else {
                    vibrator.cancel();
                    monitorWarningBean.setIsShakeOpen(false);
//                    monitorWarningBeanDao.update(monitorWarningBean);
                }
            }
        });
    }

    private void setIsVoiceOpenListener() {
        if (monitorWarningBean.getIsVoiceOpen()) {
            isVoiceOpen.setChecked(true);
        } else {
            isVoiceOpen.setChecked(false);
        }
        isVoiceOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                int ringerVolume = 30;
                if (isChecked) {
                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audio.setStreamVolume(AudioManager.RINGER_MODE_NORMAL, ringerVolume, 0);
                    monitorWarningBean.setIsVoiceOpen(true);
//                    monitorWarningBeanDao.update(monitorWarningBean);
                } else {
                    audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    monitorWarningBean.setIsVoiceOpen(false);
//                    monitorWarningBeanDao.update(monitorWarningBean);
                }
            }
        });
    }

    private void setIsNotificationOpenListener() {
        if (monitorWarningBean.getIsSendingEmail()) {
            isSendEmail.setChecked(true);
        } else {
            isSendEmail.setChecked(false);
        }
        isNotificationOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationOpenLayout.show();
                    monitorWarningBean.setIsNotificationOpen(true);
//                    monitorWarningBeanDao.update(monitorWarningBean);
                } else {
                    notificationOpenLayout.hide();
                    monitorWarningBean.setIsNotificationOpen(false);
//                    monitorWarningBeanDao.update(monitorWarningBean);
                }
            }
        });
    }

    private void setIsEmailSendListener() {
        isSendEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    monitorWarningBean.setIsSendingEmail(true);
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
                                        Log.d("是否接受短信", "success");
                                    } else {
                                        Log.d("是否接受短信", "failed");
                                        Toast.makeText(getContext(), "修改失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    monitorWarningBean.setIsSendingEmail(false);
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
                                        Log.d("是否接受短信", "success");
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


}
