package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.Views.NoScrollGridView;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhongsz on 2016/12/7.
 */

public class TimeLineAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] dates;
    private String boatName;

    public TimeLineAdapter(Context context, String[] dates, String boatName) {
        super(context, R.layout.adapter_timeline, dates);
        this.context = context;
        this.dates = dates;
        this.boatName = boatName;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_timeline, parent, false);
        }

        NoScrollGridView timelineGridView = (NoScrollGridView) convertView.findViewById(R.id.timeline_gridview);
        PhotosAdapter adapter = new PhotosAdapter(context, loadDateImagePaths(dates[position]), boatName);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        timelineGridView.setAdapter(adapter);

        TextView time = (TextView) convertView.findViewById(R.id.timeline_time);
        time.setText(dates[position]);

        return convertView;
    }

    private String[] loadDateImagePaths(String date) {
        ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        QueryBuilder queryBuilder = imageMessageDao.queryBuilder();
        List<ImageMessage> imagePaths = queryBuilder.where(ImageMessageDao.Properties.AddTime.eq(date)).where(ImageMessageDao.Properties.ParentDir.eq(boatName)).list();
        String[] imageUrls;
        List<String> imageUrlsCopy = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            imageUrlsCopy.add(i, "/sdcard/VideoApp/" + boatName + "/" + date + "/" + imagePaths.get(i).getImgPath());
        }
        imageUrls = imageUrlsCopy.toArray(new String[imageUrlsCopy.size()]);
        return imageUrls;
    }
}
