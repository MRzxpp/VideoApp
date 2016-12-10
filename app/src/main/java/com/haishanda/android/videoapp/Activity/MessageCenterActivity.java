package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/14.
 */

public class MessageCenterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToLastPage(View view) {
        this.finish();
    }
}
