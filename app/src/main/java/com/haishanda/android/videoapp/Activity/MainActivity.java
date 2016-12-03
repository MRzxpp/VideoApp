package com.haishanda.android.videoapp.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.haishanda.android.videoapp.Bean.AlarmNum;
import com.haishanda.android.videoapp.Fragement.BoatFragment;
import com.haishanda.android.videoapp.Fragement.MonitorFragment;
import com.haishanda.android.videoapp.Fragement.MyFragment;
import com.haishanda.android.videoapp.Fragement.PhotosFragment;
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
import butterknife.OnClick;

public class MainActivity extends FragmentActivity implements BottomNavigationBar.OnTabSelectedListener {
    @BindColor(R.color.textGrey)
    int textGrey;
    @BindColor(R.color.textBlue)
    int textBlue;
    @BindView(R.id.bottom_navigation_bar_mainactivity)
    BottomNavigationBar navigationBar;
    //    @BindView(R.id.boat)
//    TextView boatText;
//    @BindView(R.id.photos)
//    TextView photosText;
//    @BindView(R.id.monitor)
//    TextView monitorText;
//    @BindView(R.id.myApp)
//    TextView myAppText;
//    @BindView(R.id.boat_img)
//    ImageView boatImg;
//    @BindView(R.id.photos_img)
//    ImageView photoImg;
//    @BindView(R.id.monitor_img)
//    ImageView monitorImg;
//    @BindView(R.id.myApp_img)
//    ImageView myAppImg;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragments = getFragments();
        setDefaultFragment();
        navigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        navigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
        QueryBuilder<AlarmNum> queryBuilder = alarmNumDao.queryBuilder();
        AlarmNum alarmNum;
        try {
            alarmNum = queryBuilder.unique();
        } catch (DaoException e) {
            alarmNum = new AlarmNum(0);
        }
        BadgeItem numberBadgeItem = new BadgeItem()
                .setBorderWidth(5)
                .setBackgroundColorResource(R.color.red)
//                .setText(String.valueOf(alarmNum.getAlarmNum()))
                .setText(String.valueOf(alarmNum.getAlarmNum()))
                .setHideOnSelect(true);
        navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue).setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                .setFirstSelectedPosition(0)
                .initialise();
        navigationBar.setTabSelectedListener(this);
    }

    public void refresh(int position) {
        navigationBar.clearAll();
        AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
        QueryBuilder<AlarmNum> queryBuilder = alarmNumDao.queryBuilder();
        AlarmNum alarmNum;
        try {
            alarmNum = queryBuilder.unique();
        } catch (DaoException e) {
            alarmNum = new AlarmNum(0);
        }
        if (alarmNum.getAlarmNum() != 0) {
            BadgeItem numberBadgeItem = new BadgeItem()
                    .setBorderWidth(5)
                    .setBackgroundColorResource(R.color.red)
//                .setText(String.valueOf(alarmNum.getAlarmNum()))
                    .setText(String.valueOf(alarmNum.getAlarmNum()))
                    .setHideOnSelect(true);
            navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue).setBadgeItem(numberBadgeItem))
                    .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                    .setFirstSelectedPosition(position)
                    .initialise();
            navigationBar.setTabSelectedListener(this);
        } else {
            navigationBar.addItem(new BottomNavigationItem(boatPick, "船舶").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(photosPick, "相册").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(monitorPick, "监控").setActiveColorResource(R.color.textBlue))
                    .addItem(new BottomNavigationItem(myPick, "我的").setActiveColorResource(R.color.textBlue))
                    .setFirstSelectedPosition(position)
                    .initialise();
            navigationBar.setTabSelectedListener(this);
        }

    }


    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        BoatFragment boatFragment = new BoatFragment();
        transaction.replace(R.id.fragment_main, boatFragment);
        transaction.commit();
    }

//    TextView lastPickedText;
//    ImageView lastPickedImg;
//
//    @OnClick({R.id.to_boat_fragment, R.id.to_photos_fragment, R.id.to_monitor_fragment, R.id.to_my_fragment})
//    public void skipToBoatFragment(View view) {
//
//        if (lastPickedText == null) {
//            lastPickedText = boatText;
//        }
//        if (lastPickedImg == null) {
//            lastPickedImg = boatImg;
//        }
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        switch (view.getId()) {
//            case (R.id.to_boat_fragment): {
//                BoatFragment boatFragment = new BoatFragment();
//                fragmentTransaction.replace(R.id.fragment_main, boatFragment);
//                lastPickedText.setTextColor(textGrey);
//                setChangedImg();
//                boatText.setTextColor(textBlue);
//                boatImg.setImageDrawable(boatPick);
//                lastPickedText = boatText;
//                lastPickedImg = boatImg;
//                break;
//            }
//            case (R.id.to_photos_fragment): {
//                PhotosIndexFragment photosIndexFragment = new PhotosIndexFragment();
//                fragmentTransaction.replace(R.id.fragment_main, photosIndexFragment);
//                lastPickedText.setTextColor(textGrey);
//                setChangedImg();
//                photosText.setTextColor(textBlue);
//                photoImg.setImageDrawable(photosPick);
//                lastPickedText = photosText;
//                lastPickedImg = photoImg;
//                break;
//            }
//            case (R.id.to_monitor_fragment): {
//                MonitorFragment monitorFragment = new MonitorFragment();
//                fragmentTransaction.replace(R.id.fragment_main, monitorFragment);
//                lastPickedText.setTextColor(textGrey);
//                setChangedImg();
//                monitorText.setTextColor(textBlue);
//                monitorImg.setImageDrawable(monitorPick);
//                lastPickedText = monitorText;
//                lastPickedImg = monitorImg;
//                break;
//            }
//            case (R.id.to_my_fragment): {
//                MyFragment myFragment = new MyFragment();
//                fragmentTransaction.replace(R.id.fragment_main, myFragment, "myFragment");
//                lastPickedText.setTextColor(textGrey);
//                setChangedImg();
//                myAppText.setTextColor(textBlue);
//                myAppImg.setImageDrawable(myPick);
//                lastPickedText = myAppText;
//                lastPickedImg = myAppImg;
//                break;
//            }
//            default:
//                break;
//        }
//        fragmentTransaction.commit();
//
//    }
//
//    private void setChangedImg() {
//        if (lastPickedImg == boatImg) {
//            lastPickedImg.setImageDrawable(boatUnPick);
//        }
//        if (lastPickedImg == photoImg) {
//            lastPickedImg.setImageDrawable(photosUnPick);
//        }
//        if (lastPickedImg == monitorImg) {
//            lastPickedImg.setImageDrawable(monitorUnPick);
//        }
//        if (lastPickedImg == myAppImg) {
//            lastPickedImg.setImageDrawable(myUnPick);
//        }
//
//    }

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
