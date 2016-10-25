package com.haishanda.android.videoapp.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.Fragement.BoatFragment;
import com.haishanda.android.videoapp.Fragement.MonitorFragment;
import com.haishanda.android.videoapp.Fragement.MyFragment;
import com.haishanda.android.videoapp.Fragement.PhotosIndexFragment;
import com.haishanda.android.videoapp.R;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {
    @BindColor(R.color.textGrey)
    int textGrey;
    @BindColor(R.color.textBlue)
    int textBlue;
    @BindView(R.id.boat)
    TextView boatText;
    @BindView(R.id.photos)
    TextView photosText;
    @BindView(R.id.monitor)
    TextView monitorText;
    @BindView(R.id.myApp)
    TextView myAppText;
    @BindView(R.id.boat_img)
    ImageView boatImg;
    @BindView(R.id.photos_img)
    ImageView photoImg;
    @BindView(R.id.monitor_img)
    ImageView monitorImg;
    @BindView(R.id.myApp_img)
    ImageView myAppImg;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setDefaultFragment();
    }

    public void skipToLoginPage(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        BoatFragment boatFragment = new BoatFragment();
        transaction.replace(R.id.fragment_main, boatFragment);
        transaction.commit();
    }

    TextView lastPickedText;
    ImageView lastPickedImg;

    @OnClick({R.id.to_boat_fragment, R.id.to_photos_fragment, R.id.to_monitor_fragment, R.id.to_my_fragment})
    public void skipToBoatFragment(View view) {

        if (lastPickedText == null) {
            lastPickedText = boatText;
        }
        if (lastPickedImg == null) {
            lastPickedImg = boatImg;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case (R.id.to_boat_fragment): {
                BoatFragment boatFragment = new BoatFragment();
                fragmentTransaction.replace(R.id.fragment_main, boatFragment);
                lastPickedText.setTextColor(textGrey);
                setChangedImg();
                boatText.setTextColor(textBlue);
                boatImg.setImageDrawable(boatPick);
                lastPickedText = boatText;
                lastPickedImg = boatImg;
                break;
            }
            case (R.id.to_photos_fragment): {
                PhotosIndexFragment photosIndexFragment = new PhotosIndexFragment();
                fragmentTransaction.replace(R.id.fragment_main, photosIndexFragment);
                lastPickedText.setTextColor(textGrey);
                setChangedImg();
                photosText.setTextColor(textBlue);
                photoImg.setImageDrawable(photosPick);
                lastPickedText = photosText;
                lastPickedImg = photoImg;
                break;
            }
            case (R.id.to_monitor_fragment): {
                MonitorFragment monitorFragment = new MonitorFragment();
                fragmentTransaction.replace(R.id.fragment_main, monitorFragment);
                lastPickedText.setTextColor(textGrey);
                setChangedImg();
                monitorText.setTextColor(textBlue);
                monitorImg.setImageDrawable(monitorPick);
                lastPickedText = monitorText;
                lastPickedImg = monitorImg;
                break;
            }
            case (R.id.to_my_fragment): {
                MyFragment myFragment = new MyFragment();
                fragmentTransaction.replace(R.id.fragment_main, myFragment);
                lastPickedText.setTextColor(textGrey);
                setChangedImg();
                myAppText.setTextColor(textBlue);
                myAppImg.setImageDrawable(myPick);
                lastPickedText = myAppText;
                lastPickedImg = myAppImg;
                break;
            }
            default:
                break;
        }
        fragmentTransaction.commit();

    }

    private void setChangedImg() {
        if (lastPickedImg == boatImg) {
            lastPickedImg.setImageDrawable(boatUnPick);
        }
        if (lastPickedImg == photoImg) {
            lastPickedImg.setImageDrawable(photosUnPick);
        }
        if (lastPickedImg == monitorImg) {
            lastPickedImg.setImageDrawable(monitorUnPick);
        }
        if (lastPickedImg == myAppImg) {
            lastPickedImg.setImageDrawable(myUnPick);
        }

    }

}
