package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.haishanda.android.videoapp.Activity.PlayVideoActivity;
import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/19.
 */

public class CamerasFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cameras, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.test1)
    public void playVideo(View view) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), PlayVideoActivity.class);
        startActivity(intent);
    }
}