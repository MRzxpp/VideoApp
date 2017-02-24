package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.haishanda.android.videoapp.R;
import com.pgyersdk.update.PgyUpdateManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 *
 * Created by Zhongsz on 2016/11/14.
 */

public class MessageCenterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        ButterKnife.bind(this);
        PgyUpdateManager.register(this);
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToLastPage(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        PgyUpdateManager.unregister();
    }

    @OnCheckedChanged(R.id.switch_test_path)
    public void switchTestPath(boolean checked) {
        SharedPreferences preferences = getSharedPreferences("TEST_PATH", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (checked) {
            editor.putBoolean("TEST_PATH_ON", true);
            Log.d("TEST_PATH", "ON");
        } else {
            editor.putBoolean("TEST_PATH_ON", false);
            Log.d("TEST_PATH", "OFF");
        }
        editor.apply();
    }

}
