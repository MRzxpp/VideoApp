package com.haishanda.android.videoapp.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.haishanda.android.videoapp.Fragement.AboutBoatFragment;
import com.haishanda.android.videoapp.Fragement.DeleteBoatFragment;
import com.haishanda.android.videoapp.Fragement.RenameBoatFragment;
import com.haishanda.android.videoapp.Fragement.ResetBoatPasswordFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;

import butterknife.BindView;
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

    @BindView(R.id.current_boatname)
    TextView currentBoatname;

    public BoatConfigActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_config);
        ButterKnife.bind(this);
        instance = this;
        extra = getIntent().getExtras();
        boatName = extra.getString("boatName");
        machineId = extra.getInt("machineId");
        globalId = extra.getString("globalId");
    }

    @Override
    protected void onResume() {
        super.onResume();
        boatName = VideoApplication.getApplication().getCurrentBoatName();
        currentBoatname.setText(boatName);
    }

    public void refresh() {
        onResume();
    }

    @OnClick(R.id.back_to_boat_fragment_btn)
    public void backToLastPage(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
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
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.boat_config_layout, aboutBoatFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.rename_boat_layout)
    public void skipToRenameBoatFragment() {
        Bundle data = new Bundle();
        data.putInt("machineId", machineId);
        data.putString("boatName", boatName);
        RenameBoatFragment renameBoatFragment = new RenameBoatFragment();
        renameBoatFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
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
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.boat_config_layout, resetBoatPasswordFragment);
        fragmentTransaction.commit();

    }

    @OnClick(R.id.delete_boat_btn)
    public void deleteBoat() {
        Bundle data = new Bundle();
        data.putString("boatName", boatName);
        data.putInt("machineId", machineId);
        DeleteBoatFragment deleteBoatFragment = new DeleteBoatFragment();
        deleteBoatFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.boat_config_layout, deleteBoatFragment);
        fragmentTransaction.commit();
    }
}
