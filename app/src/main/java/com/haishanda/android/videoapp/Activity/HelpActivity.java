package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;

import com.haishanda.android.videoapp.R;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 帮助与反馈
 * Created by Zhongsz on 2016/12/15.
 */

public class HelpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PgyFeedbackShakeManager.unregister();
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToLastPage() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
