package com.haishanda.android.videoapp.Adapter;

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
import com.haishanda.android.videoapp.Activity.PlayPhotoActivity;
import com.haishanda.android.videoapp.R;


/**
 * 相册界面的图标适配器
 * Created by Zhongsz on 2016/10/29.
 */

class PhotosAdapter extends ArrayAdapter {


    private Context context;
    private LayoutInflater inflater;

    private String[] imagePath;
    private String boatName;

    PhotosAdapter(Context context, String[] imagePath, String boatName) {
        super(context, R.layout.adapter_photos, imagePath);

        this.context = context;
        this.imagePath = imagePath;
        this.boatName = boatName;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_photos, parent, false);
        }

        Glide
                .with(context)
                .load(imagePath[position])
                .into((ImageView) convertView.findViewById(R.id.photo_adapter));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("imagePath", imagePath[position]);
                intent.putExtra("boatName", boatName);
                intent.setClass(context, PlayPhotoActivity.class);
                context.startActivity(intent);

            }
        });

        TextView boatNameView = (TextView) convertView.findViewById(R.id.boat_name_text);
        boatNameView.setText(boatName);

        return convertView;
    }
}
