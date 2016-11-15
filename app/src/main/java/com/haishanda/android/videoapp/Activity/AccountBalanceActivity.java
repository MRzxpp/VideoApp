package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;

import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/14.
 */

public class AccountBalanceActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_balance);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToLastPafe() {
        this.finish();
    }
}
