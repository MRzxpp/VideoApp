package com.haishanda.android.videoapp.utils.textwatcher;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

import static com.haishanda.android.videoapp.config.Constant.ENABLED;

/**
 * 通过正则表达式判断输入是否满足电话号码格式的工具类
 * Created by Zhongsz on 2016/10/28.
 */

public class FetchCodeWatcher implements TextWatcher {

    private final EditText phoneNum;
    private final Button button;
    private final Drawable blue;
    private final Drawable gray;
    private final int textGray;
    private final int white;

    public FetchCodeWatcher(EditText phoneNum, Button button, Drawable blue, Drawable gray, int textGray, int white) {
        this.phoneNum = phoneNum;
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
        Pattern phoneNumPattern = Pattern.compile("^[1][358][0-9]{9}$");
        Matcher phoneNumMatcher = phoneNumPattern.matcher(phoneNum.getText().toString());
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


}
