package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/10/25.
 */

public class VideoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
