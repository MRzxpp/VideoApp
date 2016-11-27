package com.haishanda.android.videoapp.Fragement;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ExpandableLayout;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    public static final int RINGER_MODE_SILENT = 0;
    public static final int RINGER_MODE_VIBRATE = 1;
    public static final int RINGER_MODE_NORMAL = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_warning, container, false);
        ButterKnife.bind(this, view);
        setIsNotificationOpenListener();
        return view;
    }

    private void setIsShakeOpenListener() {
        isShakeOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {1000, 2000, 1000, 3000};
                if (isChecked) {
                    vibrator.vibrate(pattern, -1);
                    vibrator.cancel();
                } else {
                    vibrator.cancel();
                }
            }
        });
    }

    private void setIsVoiceOpenListener() {
        isVoiceOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                int ringerVolume = 30;
                if (isChecked) {
                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audio.setStreamVolume(AudioManager.RINGER_MODE_NORMAL, ringerVolume, 0);
                } else {
                    audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
        });
    }

    private void setIsNotificationOpenListener() {
        isNotificationOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationOpenLayout.show();
                } else {
                    notificationOpenLayout.hide();
                }
            }
        });
    }

    @OnClick(R.id.back_to_monitor_config_btn)
    public void backToMonitorConfigPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }


}
