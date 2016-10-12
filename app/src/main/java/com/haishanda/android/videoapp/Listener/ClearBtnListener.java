package com.haishanda.android.videoapp.Listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * Created by Zhongsz on 2016/10/11.
 */

public class ClearBtnListener implements TextWatcher {
    private CharSequence temp;//监听前的文本
    private ImageView clearBtn;
    private EditText editText;

    public ClearBtnListener(ImageView clearBtn, EditText editText) {
        this.clearBtn = clearBtn;
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        temp = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!editText.getText().toString().equals("")) {
            clearBtn.setVisibility(View.VISIBLE);
        } else {
            clearBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
