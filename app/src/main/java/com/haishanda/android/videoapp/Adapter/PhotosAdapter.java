package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.R;

/**
 * Created by Zhongsz on 2016/10/29.
 */

public class PhotosAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private String[] imagePath;

    public PhotosAdapter(Context context, String[] imagePath) {
        super(context, R.layout.adapter_photos, imagePath);

        this.context = context;
        this.imagePath = imagePath;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_photos, parent, false);
        }

        Glide
                .with(context)
                .load(imagePath[position])
                .into((ImageView) convertView);

        return convertView;
    }
}
