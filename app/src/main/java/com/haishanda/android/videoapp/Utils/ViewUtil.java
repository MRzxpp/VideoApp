package com.haishanda.android.videoapp.utils;

import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 改变密码的可见性
 * 用在登录与注册界面中
 * Created by Zhongsz on 2016/10/13.
 */

public class ViewUtil {
    public static void changeVisiable(TextView textView, EditText editText) {
        if (textView.getText() != "隐藏密码") {
            textView.setText("隐藏密码");
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            textView.setText("显示密码");
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
}
