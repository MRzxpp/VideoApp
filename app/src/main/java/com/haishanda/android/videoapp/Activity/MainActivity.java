package com.haishanda.android.videoapp.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.haishanda.android.videoapp.Bean.AlarmNum;
import com.haishanda.android.videoapp.Fragement.BoatFragment;
import com.haishanda.android.videoapp.Fragement.MonitorFragment;
import com.haishanda.android.videoapp.Fragement.MyFragment;
import com.haishanda.android.videoapp.Fragement.PhotosIndexFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.AlarmNumDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

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
    public static MainActivity instance;

    private Handler handler;

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
        AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
        QueryBuilder<AlarmNum> queryBuilder = alarmNumDao.queryBuilder();
        AlarmNum alarmNum;
        try {
            alarmNum = queryBuilder.unique();
        } catch (DaoException e) {
            alarmNum = new AlarmNum(0);
        }
        if (alarmNum == null) {
            alarmNum = new AlarmNum(0);
        }
        if (alarmNum.getAlarmNum() != 0) {
            BadgeItem numberBadgeItem = new BadgeItem()
                    .setBorderWidth(5)
                    .setBackgroundColorResource(R.color.red)
                    .setText(String.valueOf(alarmNum.getAlarmNum()))
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
            AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
            QueryBuilder<AlarmNum> queryBuilder = alarmNumDao.queryBuilder();
            AlarmNum alarmNum;
            try {
                alarmNum = queryBuilder.unique();
            } catch (DaoException e) {
                alarmNum = new AlarmNum(0);
            }
            if (alarmNum == null) {
                alarmNum = new AlarmNum(0);
            }
            if (alarmNum.getAlarmNum() != 0) {
                BadgeItem numberBadgeItem = new BadgeItem()
                        .setBorderWidth(5)
                        .setBackgroundColorResource(R.color.red)
                        .setText(String.valueOf(alarmNum.getAlarmNum()))
                        .setHideOnSelect(true);
                navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                        .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                        .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue).setBadgeItem(numberBadgeItem))
                        .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                        .setFirstSelectedPosition(0)
                        .initialise();
                navigationBar.setTabSelectedListener(MainActivity.instance);
                return;
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
}
