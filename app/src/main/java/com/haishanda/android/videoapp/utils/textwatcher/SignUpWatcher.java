package com.haishanda.android.videoapp.utils.textwatcher;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;

import static com.haishanda.android.videoapp.config.Constant.ENABLED;

/**
 * 当用户的输入满足条件时，登录键状态改变
 * Created by Zhongsz on 2016/10/28.
 */

public class SignUpWatcher implements TextWatcher {
    private final EditText username;
    private final EditText password;
    private final EditText repassword;
    private final EditText fetchcode;
    private final Button button;
    private final Drawable blue;
    private final Drawable gray;
    private final int textGray;
    private final int white;

    public SignUpWatcher(EditText username, EditText password, EditText repassword, EditText fetchcode, Button button, Drawable blue, Drawable gray, int textGray, int white) {
        this.username = username;
        this.password = password;
        this.repassword = repassword;
        this.fetchcode = fetchcode;
        this.button = button;
        this.blue = blue;
        this.gray = gray;
        this.textGray = textGray;
        this.white = white;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!username.getText().toString().equals("") && !password.getText().toString().equals("")
                && !repassword.getText().toString().equals("") && !fetchcode.getText().toString().equals("")) {
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
}
