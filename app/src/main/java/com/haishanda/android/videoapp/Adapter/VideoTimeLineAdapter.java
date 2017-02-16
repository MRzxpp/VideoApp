package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haishanda.android.videoapp.Bean.VideoMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.Views.NoScrollGridView;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地视频时间线所对应的listview adapter
 * Created by Zhongsz on 2016/12/24.
 */

public class VideoTimeLineAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] times;
    private String boatName;
    private String[] dates;
    private List<String> shortPaths = new ArrayList<>();

    public VideoTimeLineAdapter(Context context, String[] dates, String boatName, String[] times) {
        super(context, R.layout.adapter_timeline, dates);
        this.context = context;
        this.times = times;
        this.boatName = boatName;
        this.dates = dates;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_timeline, parent, false);
        }

        NoScrollGridView timelineGridView = (NoScrollGridView) convertView.findViewById(R.id.timeline_gridview);
        VideosAdapter adapter = new VideosAdapter(context, loadDateVideoPaths(dates[position]), boatName, loadDateIconPaths(dates[position]), shortPaths);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        timelineGridView.setAdapter(adapter);

        TextView time = (TextView) convertView.findViewById(R.id.timeline_time);
        time.setText(times[position]);

        return convertView;
    }

    private String[] loadDateIconPaths(String date) {
        VideoMessageDao videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
        QueryBuilder queryBuilder = videoMessageDao.queryBuilder();
        List<VideoMessage> videoMessages = queryBuilder.where(VideoMessageDao.Properties.AddDate.eq(date)).where(VideoMessageDao.Properties.ParentDir.eq(boatName)).list();
        String[] iconUrls;
        List<String> iconUrlsCopy = new ArrayList<>();
        for (int i = 0; i < videoMessages.size(); i++) {
            iconUrlsCopy.add(i, videoMessages.get(i).getIconPath());
        }
        iconUrls = iconUrlsCopy.toArray(new String[iconUrlsCopy.size()]);
        return iconUrls;
    }

    private String[] loadDateVideoPaths(String date) {
        VideoMessageDao videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
        QueryBuilder queryBuilder = videoMessageDao.queryBuilder();
        List<VideoMessage> videoMessages = queryBuilder.where(VideoMessageDao.Properties.AddDate.eq(date)).where(VideoMessageDao.Properties.ParentDir.eq(boatName)).list();
        String[] videoUrls;
        List<String> videoUrlsCopy = new ArrayList<>();
        for (int i = 0; i < videoMessages.size(); i++) {
            videoUrlsCopy.add(i, Environment.getExternalStorageDirectory().getPath() + "/VideoApp/" + boatName + "/" + date + "/Videos/" + videoMessages.get(i).getVideoPath());
            shortPaths.add(videoMessages.get(i).getVideoPath());
        }
        videoUrls = videoUrlsCopy.toArray(new String[videoUrlsCopy.size()]);
        return videoUrls;
    }
}
