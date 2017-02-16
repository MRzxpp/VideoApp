package com.haishanda.android.videoapp.Activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.haishanda.android.videoapp.Config.Constant;
import com.haishanda.android.videoapp.Fragement.BoatFragment;
import com.haishanda.android.videoapp.Fragement.MonitorFragment;
import com.haishanda.android.videoapp.Fragement.MyFragment;
import com.haishanda.android.videoapp.Fragement.PhotosIndexFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Service.LoginService;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.AlarmVoBeanDao;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity implements BottomNavigationBar.OnTabSelectedListener {
    @BindColor(R.color.textGrey)
    int textGrey;
    @BindColor(R.color.textBlue)
    int textBlue;
    @BindView(R.id.bottom_navigation_bar_mainactivity)
    BottomNavigationBar navigationBar;
    @BindDrawable(R.drawable.boat_pick)
    Drawable boatPick;
    @BindDrawable(R.drawable.boat_unpick)
    Drawable boatUnPick;
    @BindDrawable(R.drawable.photos_pick)
    Drawable photosPick;
    @BindDrawable(R.drawable.photos_unpick)
    Drawable photosUnPick;
    @BindDrawable(R.drawable.monitor_pick)
    Drawable monitorPick;
    @BindDrawable(R.drawable.monitor_unpick)
    Drawable monitorUnPick;
    @BindDrawable(R.drawable.my_pick)
    Drawable myPick;
    @BindDrawable(R.drawable.my_unpick)
    Drawable myUnPick;

    private ArrayList<Fragment> fragments;
    private static MainActivity instance;

    private Handler handler;
    private EMErrorReceiver receiver;
    private boolean isRegistered;
    private SharedPreferences alarmPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        handler = new Handler();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragments = getFragments();
        setDefaultFragment();
        navigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        navigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        alarmPreferences = getSharedPreferences(Constant.ALARM_MESSAGE, MODE_PRIVATE);
        int alarmNumber = alarmPreferences.getInt(Constant.ALARM_MESSAGE_NUMBER, 0);
        if (alarmNumber != 0) {
            BadgeItem numberBadgeItem = new BadgeItem()
                    .setBorderWidth(5)
                    .setBackgroundColorResource(R.color.red)
                    .setText(String.valueOf(alarmNumber))
                    .setHideOnSelect(true);
            navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue).setBadgeItem(numberBadgeItem))
                    .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                    .setFirstSelectedPosition(0)
                    .initialise();
            navigationBar.setTabSelectedListener(this);
        } else {
            navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                    .setFirstSelectedPosition(0)
                    .initialise();
            navigationBar.setTabSelectedListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册广播接收器
        IntentFilter filter = new IntentFilter(Constant.ACTION_RECEIVE_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new EMErrorReceiver();
        registerReceiver(receiver, filter);
        isRegistered = true;
    }

    public static MainActivity getInstance() {
        if (instance == null) {
            synchronized (MainActivity.class) {
                if (instance == null) {
                    instance = new MainActivity();
                }
            }
        }
        return instance;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRegistered) {
            unregisterReceiver(receiver);
        }
    }

    public void refresh() {
        new Thread() {
            public void run() {
                handler.post(updateUI);
            }
        }.start();
    }

    Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            navigationBar.clearAll();
            int alarmNumber = alarmPreferences.getInt(Constant.ALARM_MESSAGE_NUMBER, 0);
            if (alarmNumber != 0) {
                BadgeItem numberBadgeItem = new BadgeItem()
                        .setBorderWidth(5)
                        .setBackgroundColorResource(R.color.red)
                        .setText(String.valueOf(alarmNumber))
                        .setHideOnSelect(true);
                navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                        .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                        .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue).setBadgeItem(numberBadgeItem))
                        .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                        .setFirstSelectedPosition(0)
                        .initialise();
                navigationBar.setTabSelectedListener(MainActivity.instance);
            } else {
                navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                        .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                        .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue))
                        .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                        .setFirstSelectedPosition(2)
                        .initialise();
                navigationBar.setTabSelectedListener(MainActivity.instance);
            }
        }
    };


    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        BoatFragment boatFragment = new BoatFragment();
        transaction.replace(R.id.fragment_main, boatFragment);
        transaction.commit();
    }

    private ArrayList<Fragment> getFragments() {
        BoatFragment boatFragment = new BoatFragment();
        PhotosIndexFragment photosFragment = new PhotosIndexFragment();
        MonitorFragment monitorFragment = new MonitorFragment();
        MyFragment myFragment = new MyFragment();
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(boatFragment);
        fragments.add(photosFragment);
        fragments.add(monitorFragment);
        fragments.add(myFragment);
        return fragments;
    }

    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.replace(R.id.fragment_main, fragment);
                } else {
                    ft.add(R.id.fragment_main, fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {

    }

    //接收广播类
    public class EMErrorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String loginMessage = intent.getStringExtra("loginMessage");
            if (loginMessage.equals(Constant.EMERROR_CONFLICT) || loginMessage.equals(Constant.EMERROR_CLIENT_REMOVED)) {
                logoutAndDeleteLoginMessage();
                Log.d("EMERROR", "环信账户连接失败,账号被迫下线 : " + loginMessage);                // 结束该Activity
                finish();
                // 注销广播接收器
                context.unregisterReceiver(this);
                isRegistered = false;
            }
            if (loginMessage.equals(Constant.EMERROR_CHAT_FAILED) || loginMessage.equals(Constant.EMERROR_DISCONNECT)) {
                Log.d("EMERROR", "环信账户连接失败 : " + loginMessage);
            }
        }
    }

    private void logoutAndDeleteLoginMessage() {
        //监控数目重置
        AlarmVoBeanDao alarmVoBeanDao = VideoApplication.getApplication().getDaoSession().getAlarmVoBeanDao();
        alarmVoBeanDao.deleteAll();
        //清除监控配置信息
        MonitorConfigBeanDao monitorConfigBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorConfigBeanDao();
        monitorConfigBeanDao.deleteAll();
        //报警数目归零
        SharedPreferences alarmPreferences = getSharedPreferences(Constant.ALARM_MESSAGE, MODE_PRIVATE);
        SharedPreferences.Editor alarmEditor = alarmPreferences.edit();
        alarmEditor.remove(Constant.ALARM_MESSAGE_NUMBER);
        alarmEditor.apply();
        //清除登录信息
        SharedPreferences preferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constant.USER_PREFERENCE_ID);
        editor.remove(Constant.USER_PREFERENCE_USERNAME);
        editor.remove(Constant.USER_PREFERENCE_TOKEN);
        editor.apply();
        //重置VideoApplication
        VideoApplication.getApplication().setCurrentBoatName(null);
        VideoApplication.getApplication().setCurrentMachineId(-1);
        Thread emThread = new Thread(new EMThread());
        emThread.start();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        Intent serviceIntent = new Intent(this, LoginService.class);
        stopService(serviceIntent);
    }

    class EMThread implements Runnable {
        @Override
        public void run() {
            EMClient.getInstance().logout(true);
        }
    }
}
