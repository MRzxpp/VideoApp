package com.haishanda.android.videoapp.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;


import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.config.StringConstant;
import com.haishanda.android.videoapp.fragment.AboutBoatFragment;
import com.haishanda.android.videoapp.fragment.DeleteBoatFragment;
import com.haishanda.android.videoapp.fragment.RenameBoatFragment;
import com.haishanda.android.videoapp.fragment.ResetBoatBindingPasswordFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 船舶配置界面
 * Created by Zhongsz on 2016/10/25.
 */

public class BoatConfigActivity extends FragmentActivity {
    private String boatName;
    private int machineId;
    private String netModuleSerialNumber;

    @BindView(R.id.current_boatname)
    TextView currentBoatName;

    public BoatConfigActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_config);
        ButterKnife.bind(this);
        instance = this;
        Bundle extra = getIntent().getExtras();
        boatName = extra.getString(StringConstant.INTENT_BOAT_NAME);
        machineId = extra.getInt(StringConstant.INTENT_MACHINE_ID);
        netModuleSerialNumber = extra.getString(StringConstant.INTENT_NET_MODULE_SERIAL_NUMBER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boatName = VideoApplication.getApplication().getCurrentBoatName();
        currentBoatName.setText(boatName);
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
        data.putString(StringConstant.INTENT_BOAT_NAME, boatName);
        data.putString(StringConstant.INTENT_NET_MODULE_SERIAL_NUMBER, netModuleSerialNumber);
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
        data.putInt(StringConstant.INTENT_MACHINE_ID, machineId);
        data.putString(StringConstant.INTENT_BOAT_NAME, boatName);
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
        data.putInt(StringConstant.INTENT_MACHINE_ID, machineId);
        ResetBoatBindingPasswordFragment resetBoatBindingPasswordFragment = new ResetBoatBindingPasswordFragment();
        resetBoatBindingPasswordFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.boat_config_layout, resetBoatBindingPasswordFragment);
        fragmentTransaction.commit();

    }

    @OnClick(R.id.delete_boat_btn)
    public void deleteBoat() {
        Bundle data = new Bundle();
        data.putString(StringConstant.INTENT_BOAT_NAME, boatName);
        data.putInt(StringConstant.INTENT_MACHINE_ID, machineId);
        DeleteBoatFragment deleteBoatFragment = new DeleteBoatFragment();
        deleteBoatFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.boat_config_layout, deleteBoatFragment);
        fragmentTransaction.commit();
    }
}
