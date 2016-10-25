package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;

import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/10/25.
 */

public class BoatConfigActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_config);
        ButterKnife.bind(this);
    }
}
