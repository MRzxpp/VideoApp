package com.haishanda.android.videoapp.Listener;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/10/28.
 */

public class FetchCodeListener implements TextWatcher {
    private CharSequence temp;//监听前的文本

    private EditText phonenum;
    private Button button;
    private Drawable blue;
    private Drawable gray;
    private int textGray;
    private int white;

    public FetchCodeListener(EditText phonenum, Button button, Drawable blue, Drawable gray, int textGray, int white) {
        this.phonenum = phonenum;
        this.button = button;
        this.blue = blue;
        this.gray = gray;
        this.textGray = textGray;
        this.white = white;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        temp = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Pattern phoneNumPattern = Pattern.compile("^[1][358][0-9]{9}$");
        Matcher phoneNumMatcher = phoneNumPattern.matcher(phonenum.getText().toString());
        if (phoneNumMatcher.matches()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(blue);
            }
            button.setTextColor(textGray);
            ButterKnife.apply(button, ENABLED, true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(gray);
            }
            button.setTextColor(white);
            ButterKnife.apply(button, ENABLED, false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };
}
