package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/11/2.
 */

public class PlayPhotoActivity extends Activity {
    Bundle extra;
    @BindView(R.id.photo_main)
    ImageView photoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_photos);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        String imagePath = extra.getString("imagePath");
        Glide
                .with(this)
                .load(imagePath)
                .into(photoMain);

    }
}
