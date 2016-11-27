package com.haishanda.android.videoapp.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Zhongsz on 2016/11/25.
 */

public class NextDayListener implements TextWatcher {
    private int beginTime;
    private int endTime;
    private TextView nextDay;

    public NextDayListener(int beginTime, int endTime, TextView nextDay) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.nextDay = nextDay;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (beginTime > endTime) {
            nextDay.setVisibility(View.VISIBLE);
        } else {
            nextDay.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
