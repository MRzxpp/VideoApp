package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/14.
 */

public class PhotosIndexFragment extends Fragment {
    @BindView(R.id.photos_fragment_title_videos)
    TextView videoTitle;
    @BindView(R.id.white_line2)
    ImageView whiteLine2;
    @BindView(R.id.photos_fragment_title_photos)
    TextView photoTitle;
    @BindView(R.id.white_line1)
    ImageView whiteLine1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_index, container, false);
        ButterKnife.bind(this, view);
        setDefaultFragment();
        return view;
    }

    @OnClick({R.id.photos_fragment_title_videos, R.id.photos_fragment_title_photos})
    public void choosePhotosOrVideos(View view) {
        switch (view.getId()) {
            case R.id.photos_fragment_title_videos: {
                whiteLine1.setVisibility(View.INVISIBLE);
                whiteLine2.setVisibility(View.VISIBLE);
                VideoFragment videoFragment = new VideoFragment();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.photos_or_videos, videoFragment);
                fragmentTransaction.commit();
                break;
            }
            case R.id.photos_fragment_title_photos: {
                whiteLine1.setVisibility(View.VISIBLE);
                whiteLine2.setVisibility(View.INVISIBLE);
                PhotosFragment photosFragment = new PhotosFragment();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.photos_or_videos, photosFragment);
                fragmentTransaction.commit();
            }
        }

    }

    public void setDefaultFragment() {
        PhotosFragment photosFragment = new PhotosFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.photos_or_videos, photosFragment);
        fragmentTransaction.commit();
    }
}
