package com.haishanda.android.videoapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haishanda.android.videoapp.adapter.TimeLineAdapter;
import com.haishanda.android.videoapp.bean.ImageMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 相册模块
 * Created by Zhongsz on 2016/10/25.
 */

public class PhotosFragment extends Fragment {
    @BindView(R.id.photos_background)
    ImageView photosBackground;
    @BindView(R.id.photos_background_text)
    TextView photosBackgroundText;
    @BindView(R.id.timeline)
    ListView timeLine;

    String[] dates = {};
    ImageMessageDao imageMessageDao;
    TimeLineAdapter adapter;
    String boatName;


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
        dates = removeRepeatedDate(loadDatesPaths(boatName));
        adapter = new TimeLineAdapter(getContext(), dates, boatName);
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        timeLine.setAdapter(adapter);
    }

    public String[] loadDatesPaths(String boatName) {
        imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        QueryBuilder queryBuilder = imageMessageDao.queryBuilder();
        List<ImageMessage> imagePaths = queryBuilder.where(ImageMessageDao.Properties.BoatName.eq(boatName)).list();
        String[] dates;
        List<String> datesCopy = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            datesCopy.add(i, imagePaths.get(i).getAddDate());
        }
        dates = datesCopy.toArray(new String[datesCopy.size()]);
        if (dates.length != 0) {
            photosBackground.setVisibility(View.INVISIBLE);
            photosBackgroundText.setVisibility(View.INVISIBLE);
        }
        return dates;
    }

    public String[] removeRepeatedDate(String[] dates) {
        String[] datesCorrect;
        ArrayList<String> datesList = new ArrayList<>();
        for (String date : dates) {
            if (!datesList.contains(date))
                datesList.add(date);
        }
        datesCorrect = datesList.toArray(new String[datesList.size()]);
        return datesCorrect;
    }
}
