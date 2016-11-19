package com.haishanda.android.videoapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.haishanda.android.videoapp.Fragement.AboutBoatFragment;
import com.haishanda.android.videoapp.Fragement.DeleteBoatFragment;
import com.haishanda.android.videoapp.Fragement.RenameBoatFragment;
import com.haishanda.android.videoapp.Fragement.ResetBoatPasswordFragment;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/25.
 */

public class BoatConfigActivity extends FragmentActivity {
    private Bundle extra;
    private String boatName;
    private int machineId;
    private String globalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_config);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        boatName = extra.getString("boatName");
        machineId = extra.getInt("machineId");
        globalId = extra.getString("globalId");

    }

    @OnClick(R.id.back_to_boat_fragment_btn)
    public void backToLastPage(View view) {
        this.finish();
    }

    @OnClick(R.id.about_boat_layout)
    public void skipToAboutBoatFragment() {
        Bundle data = new Bundle();
        data.putString("boatName", boatName);
        data.putString("globalId", globalId);
        AboutBoatFragment aboutBoatFragment = new AboutBoatFragment();
        aboutBoatFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boat_config_layout, aboutBoatFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.rename_boat_layout)
    public void skipToRenameBoatFragment() {
        RenameBoatFragment renameBoatFragment = new RenameBoatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boat_config_layout, renameBoatFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.reset_boat_pwd_layout)
    public void skipToResetPwdBoatFragment() {
        Bundle data = new Bundle();
        data.putInt("machineId", machineId);
        ResetBoatPasswordFragment resetBoatPasswordFragment = new ResetBoatPasswordFragment();
        resetBoatPasswordFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boat_config_layout, resetBoatPasswordFragment);
        fragmentTransaction.commit();

    }

    @OnClick(R.id.delete_boat_btn)
    public void deleteBoat() {
        DeleteBoatFragment deleteBoatFragment = new DeleteBoatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.boat_config_layout, deleteBoatFragment);
        fragmentTransaction.commit();
    }
}
