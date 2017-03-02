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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haishanda.android.videoapp.activity.PlayLiveActivity;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.bean.QueryCameras;


/**
 * set live
 * Created by Zhongsz on 2016/11/16.
 */
@SuppressWarnings("ResourceType")
public class LiveAdapter extends ArrayAdapter {

    private final Context context;
    private final LayoutInflater inflater;

    private final String[] imagePath;
    private final QueryCameras[] listCamera;
    private final String boatName;

    public LiveAdapter(Context context, String[] imagePath, QueryCameras[] listCamera, String boatName) {
        super(context, R.layout.adapter_live, imagePath);
        this.imagePath = imagePath;
        this.context = context;
        this.listCamera = listCamera;
        this.boatName = boatName;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_live, parent, false);
        }
        if (listCamera.length > 0) {
            Glide
                    .with(context)
                    .load(imagePath[position])
                    .error(R.drawable.boat_background)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) convertView.findViewById(R.id.live_adapter_photo));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("cameraId", listCamera[position].getId());
                    intent.putExtra("boatName", boatName);
                    intent.setClass(context, PlayLiveActivity.class);
                    context.startActivity(intent);
                }
            });

            TextView boatName = (TextView) convertView.findViewById(R.id.live_adapter_text);
            boatName.setText("摄像头" + listCamera[position].getOwner());
            boatName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        return convertView;
    }
}
