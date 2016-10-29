package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class QRCodeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.back_to_about_boat_btn)
    public void backToAboutBoatFragment() {
        AboutBoatFragment aboutBoatFragment=new AboutBoatFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.qrcode_layout, aboutBoatFragment);
        fragmentTransaction.commit();
    }
}
