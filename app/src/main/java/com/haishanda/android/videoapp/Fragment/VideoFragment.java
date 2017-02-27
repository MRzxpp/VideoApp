package com.haishanda.android.videoapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haishanda.android.videoapp.adapter.VideoTimeLineAdapter;
import com.haishanda.android.videoapp.bean.VideoMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/10/25.
 */

public class VideoFragment extends Fragment {
    @BindView(R.id.videos_background)
    ImageView videosBackground;
    @BindView(R.id.videos_background_text)
    TextView videosBackgroundText;
    @BindView(R.id.timeline_video)
    ListView timeLine;

    String[] dates = {};
    VideoMessageDao videoMessageDao;
    VideoTimeLineAdapter adapter;
    String boatName;
    String[] times = {};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        boatName = VideoApplication.getApplication().getCurrentBoatName();
        dates = removeRepeatedDate(loadDatesPaths(boatName));
        adapter = new VideoTimeLineAdapter(getContext(), dates, boatName, times);
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        timeLine.setAdapter(adapter);
    }

    public String[] loadDatesPaths(String boatName) {
        videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
        QueryBuilder queryBuilder = videoMessageDao.queryBuilder();
        List<VideoMessage> videoPaths = queryBuilder.where(VideoMessageDao.Properties.ParentDir.eq(boatName)).list();
        String[] dates;
        String[] times;
        List<String> datesCopy = new ArrayList<>();
        List<String> timesCopy = new ArrayList<>();
        for (int i = 0; i < videoPaths.size(); i++) {
            datesCopy.add(i, videoPaths.get(i).getAddDate());
            timesCopy.add(i, videoPaths.get(i).getAddTime());
        }
        dates = datesCopy.toArray(new String[datesCopy.size()]);
        times = timesCopy.toArray(new String[timesCopy.size()]);
        if (dates.length != 0) {
            videosBackground.setVisibility(View.INVISIBLE);
            videosBackgroundText.setVisibility(View.INVISIBLE);
        }
        this.times = times;
        return dates;
    }

    public String[] removeRepeatedDate(String[] dates) {
        String[] datesCorrect;
        ArrayList<String> datesList = new ArrayList<String>();
        for (int i = 0; i < dates.length; i++) {
            if (!datesList.contains(dates[i]))
                datesList.add(dates[i]);
        }
        datesCorrect = datesList.toArray(new String[datesList.size()]);
        return datesCorrect;
    }
}
