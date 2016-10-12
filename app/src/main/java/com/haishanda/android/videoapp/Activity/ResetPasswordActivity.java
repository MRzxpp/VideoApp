package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.haishanda.android.videoapp.Listener.EditChangedListener;
import com.haishanda.android.videoapp.R;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public class ResetPasswordActivity extends Activity {
    @BindView(R.id.reset_password_text)
    EditText resetPassword;
    @BindView(R.id.save_new_password_btn)
    Button savePasswordBtn;
    @BindView(R.id.eye3)
    ImageView Eye3;
    @BindDrawable(R.drawable.eyeblue)
    Drawable blueEye;
    @BindDrawable(R.drawable.eye)
    Drawable greyEye;
    @BindColor(R.color.textGrey)
    int textGrey;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        ButterKnife.bind(this);
        savePasswordBtn.setEnabled(false);
        resetPassword.addTextChangedListener(new EditChangedListener(resetPassword, savePasswordBtn, blueBtn, greyBtn, textGrey, white));
    }

    @OnClick(R.id.back_to_get_veri_btn)
    public void backToGetVeriPage(View view) {
        Intent intent = new Intent();
        intent.setClass(ResetPasswordActivity.this, GetVerificationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.eye3)
    public void setRePasswordVisiable(View view) {
        if (Eye3.getDrawable() != blueEye) {
            Eye3.setImageDrawable(blueEye);
            resetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            Eye3.setImageDrawable(greyEye);
            resetPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
}
