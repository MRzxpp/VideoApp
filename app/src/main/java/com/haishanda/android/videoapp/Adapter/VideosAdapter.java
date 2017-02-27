package com.haishanda.android.videoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.activity.PlayRecordActivity;
import com.haishanda.android.videoapp.R;

import java.io.File;
import java.util.List;

/**
 * 每一天内的视频缩略图适配器
 * Created by Zhongsz on 2016/12/24.
 */

class VideosAdapter extends ArrayAdapter {
    private final Context context;
    private final LayoutInflater inflater;

    private final String[] videoPath;
    private final String boatName;
    private final String[] iconPath;
    private final List<String> shortPaths;

    VideosAdapter(Context context, String[] videoPath, String boatName, String[] iconPath, List<String> shortPaths) {
        super(context, R.layout.adapter_videos, videoPath);

        this.context = context;
        this.videoPath = videoPath;
        this.boatName = boatName;
        this.iconPath = iconPath;
        this.shortPaths = shortPaths;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_videos, parent, false);
        }

        Glide
                .with(context)
                .load(new File(iconPath[position]))
                .into((ImageView) convertView.findViewById(R.id.video_adapter));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("videoPath", videoPath[position]);
                intent.putExtra("shortPath", shortPaths.get(position));
                intent.setClass(context, PlayRecordActivity.class);
                context.startActivity(intent);
            }
        });

        TextView boatNameView = (TextView) convertView.findViewById(R.id.boat_name_text);
        boatNameView.setText(boatName);

        return convertView;
    }
}
