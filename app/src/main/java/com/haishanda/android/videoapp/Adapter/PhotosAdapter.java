package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haishanda.android.videoapp.Activity.PlayPhotoActivity;
import com.haishanda.android.videoapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/10/29.
 */

public class PhotosAdapter extends ArrayAdapter {


    private Context context;
    private LayoutInflater inflater;

    private String[] imagePath;
    private String boatName;

    public PhotosAdapter(Context context, String[] imagePath, String boatName) {
        super(context, R.layout.adapter_photos, imagePath);

        this.context = context;
        this.imagePath = imagePath;
        this.boatName = boatName;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_photos, parent, false);
        }

        Glide
                .with(context)
                .load(imagePath[position])
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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

    public void removeItem(String singalPath) {
        List<String> imagePaths = Arrays.asList(imagePath);
        imagePaths.remove(singalPath);
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }
}
