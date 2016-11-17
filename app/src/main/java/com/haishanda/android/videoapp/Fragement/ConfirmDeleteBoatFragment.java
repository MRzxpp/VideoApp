package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class ConfirmDeleteBoatFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_delete_boat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.save_video_and_photos_btn)
    public void saveVideoAndPhotos() {
        //Todo save video and photos
    }

    @OnClick(R.id.delete_video_and_photos_btn)
    public void deleteVideoAndPhotos() {
        //Todo delete video and photos
    }
}
