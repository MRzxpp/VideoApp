package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haishanda.android.videoapp.Activity.PlayVideoActivity;
import com.haishanda.android.videoapp.R;

import java.util.List;

/**
 * Created by Zhongsz on 2016/11/16.
 */

public class LiveAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater;

    private String[] imagePath;
    private List<Long> cameraId;
    private String boatName;
    private final Drawable defaultImage = getContext().getDrawable(R.drawable.boat_background);
    private int machineId;

    public LiveAdapter(Context context, String[] imagePath, List<Long> cameraId, String boatName) {
        super(context, R.layout.adapter_live, imagePath);
        this.imagePath = imagePath;
        this.context = context;
        this.cameraId = cameraId;
        this.boatName = boatName;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_live, parent, false);
        }
        Glide
                .with(context)
                .load(imagePath[position])
                .error(defaultImage)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into((ImageView) convertView.findViewById(R.id.live_adapter_photo));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("cameraId", cameraId.get(position));
                intent.putExtra("boatName", boatName);
                intent.setClass(context, PlayVideoActivity.class);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
