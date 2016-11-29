package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Adapter.PhotosAdapter;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/10/25.
 */

public class PhotosFragment extends Fragment {
    @BindView(R.id.photos_gridview)
    GridView photosGridView;
    @BindView(R.id.photos_background)
    ImageView photosBackground;
    @BindView(R.id.photos_background_text)
    TextView photosBackgroundText;

    String[] imagePaths = {};
    ImageMessageDao imageMessageDao;
    PhotosAdapter adapter;
    String boatName;

    public PhotosAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PhotosAdapter adapter) {
        this.adapter = adapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        boatName = VideoApplication.getApplication().getCurrentBoatName();
        imagePaths = loadBoatImagePaths(boatName);
        adapter = new PhotosAdapter(getContext(), imagePaths, boatName);
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        photosGridView.setAdapter(adapter);
    }

    public String[] loadBoatImagePaths(String boatName) {
        this.boatName = boatName;
        imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        QueryBuilder queryBuilder = imageMessageDao.queryBuilder();
        List<ImageMessage> imagePaths = queryBuilder.where(ImageMessageDao.Properties.ParentDir.eq(boatName)).list();
        String[] imageUrls;
        List<String> imageUrlsCopy = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            imageUrlsCopy.add(i, "/sdcard/VideoApp/" + boatName + "/" + imagePaths.get(i).getImgPath());
        }
        imageUrls = imageUrlsCopy.toArray(new String[imageUrlsCopy.size()]);
        if (imageUrls.length != 0) {
            photosBackground.setVisibility(View.INVISIBLE);
            photosBackgroundText.setVisibility(View.INVISIBLE);
        }
        return imageUrls;
    }
}
