package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Activity.PlayPhotoActivity;
import com.haishanda.android.videoapp.Activity.PlayRecordActivity;
import com.haishanda.android.videoapp.R;

import java.io.File;
import java.util.List;

/**
 * Created by Zhongsz on 2016/12/24.
 */

public class VideosAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private String[] videoPath;
    private String boatName;
    private String[] iconPath;
    private List<String> shortPaths;

    public VideosAdapter(Context context, String[] videoPath, String boatName, String[] iconPath, List<String> shortPaths) {
        super(context, R.layout.adapter_videos, videoPath);

        this.context = context;
        this.videoPath = videoPath;
        this.boatName = boatName;
        this.iconPath = iconPath;
        this.shortPaths = shortPaths;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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
