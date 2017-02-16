package com.haishanda.android.videoapp.Utils.Watcher;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.haishanda.android.videoapp.Config.Constant;

import butterknife.ButterKnife;

/**
 * 当用户在文本框内的输入满足某种条件时，改变
 * Created by Zhongsz on 2016/10/11.
 */

public class EditChangedWatcher implements TextWatcher {

    private EditText editText;
    private Button button;
    private Drawable blue;
    private Drawable gray;
    private int textGray;
    private int white;

    public EditChangedWatcher(EditText editText, Button button, Drawable blue, Drawable gray, int textGray, int white) {
        this.editText = editText;
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
        if (!editText.getText().toString().equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(blue);
            }
            button.setTextColor(textGray);
            ButterKnife.apply(button, Constant.ENABLED, true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(gray);
            }
            button.setTextColor(white);
            ButterKnife.apply(button, Constant.ENABLED, false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
