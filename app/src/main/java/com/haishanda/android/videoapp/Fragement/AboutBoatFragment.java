package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class AboutBoatFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_boat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.to_rename_boat_fragment)
    public void skipToRenameBoatFragment() {
        RenameBoatFragment renameBoatFragment = new RenameBoatFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.about_boat_page, renameBoatFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.to_gateway_qrcode_fragment)
    public void skipToGatewayQRCodeFragment() {
        QRCodeFragment qrCodeFragment = new QRCodeFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.about_boat_page, qrCodeFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.back_to_config_btn)
    public void backToConfigPage() {
        Intent intent = new Intent(getActivity(), BoatConfigActivity.class);
        startActivity(intent);
    }
}
