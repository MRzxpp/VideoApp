package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.haishanda.android.videoapp.Fragement.AboutBoatFragment;
import com.haishanda.android.videoapp.Fragement.CamerasFragment;
import com.haishanda.android.videoapp.Fragement.RenameBoatFragment;
import com.haishanda.android.videoapp.Fragement.ResetBoatPasswordFragment;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/25.
 */

public class BoatConfigActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_config);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_to_boat_fragment_btn)
    public void backToLastPage(View view) {
        Intent intent = new Intent(BoatConfigActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.to_about_boat_fragment)
    public void skipToAboutBoatFragment() {
        AboutBoatFragment aboutBoatFragment = new AboutBoatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boat_config_layout, aboutBoatFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.to_rename_boat_fragment)
    public void skipToRenameBoatFragment() {
        RenameBoatFragment renameBoatFragment = new RenameBoatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boat_config_layout, renameBoatFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.to_resetpwd_boat_fragment)
    public void skipToResetPwdBoatFragment() {
        ResetBoatPasswordFragment resetBoatPasswordFragment = new ResetBoatPasswordFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boat_config_layout, resetBoatPasswordFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.delete_boat_btn)
    public void deleteBoat() {
        //Todo delete boat function
    }
}
