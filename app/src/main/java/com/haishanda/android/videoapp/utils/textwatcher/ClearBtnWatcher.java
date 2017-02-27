package com.haishanda.android.videoapp.utils.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * 监听用户的输入，当输入不为空时，弹出清空按钮
 * Created by Zhongsz on 2016/10/11.
 */

public class ClearBtnWatcher implements TextWatcher {
    private final ImageView clearBtn;
    private final EditText editText;


    public ClearBtnWatcher(ImageView clearBtn, EditText editText) {
        this.clearBtn = clearBtn;
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!editText.getText().toString().equals("")) {
            clearBtn.setVisibility(View.VISIBLE);
            clearBtn.setEnabled(true);
        } else {
            clearBtn.setVisibility(View.INVISIBLE);
            clearBtn.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
