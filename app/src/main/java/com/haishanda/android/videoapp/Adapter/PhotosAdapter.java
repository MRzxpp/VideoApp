package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Activity.PlayPhotoActivity;
import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_photos, parent, false);
        }

        Glide
                .with(context)
                .load(imagePath[position])
                .into((ImageView) convertView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("imagePath", imagePath[position]);
                intent.setClass(context, PlayPhotoActivity.class);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
