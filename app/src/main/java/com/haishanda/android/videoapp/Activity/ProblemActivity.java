package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/12.
 */

public class ProblemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problems);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_btn)
    public void backToLastPage() {
        this.finish();
    }
}
