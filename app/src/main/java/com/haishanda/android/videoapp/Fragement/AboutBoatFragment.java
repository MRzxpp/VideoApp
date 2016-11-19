package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class AboutBoatFragment extends Fragment {
    private String boatName;
    private String globalId;
    private Bundle data;
    @BindView(R.id.about_boat_boatname)
    TextView aboutBoatName;
    @BindView(R.id.about_boat_serial_num)
    TextView aboutBoatSerialNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_boat, container, false);
        ButterKnife.bind(this, view);
        data = this.getArguments();
        boatName = data.getString("boatName");
        globalId = data.getString("globalId");
        aboutBoatName.setText(boatName);
        aboutBoatSerialNum.setText(globalId);
        return view;
    }

    @OnClick(R.id.gateway_qrcode_layout)
    public void skipToGatewayQRCodeFragment() {
        Bundle dataQRcode = new Bundle();
        dataQRcode.putString("globalId", globalId);
        QRCodeFragment qrCodeFragment = new QRCodeFragment();
        qrCodeFragment.setArguments(dataQRcode);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.about_boat_page, qrCodeFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.back_to_config_btn)
    public void backToConfigPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
