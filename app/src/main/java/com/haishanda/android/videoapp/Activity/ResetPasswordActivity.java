package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.Listener.EditChangedListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.BindViews;
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
    TextView Eye3;
    @BindColor(R.color.btnGrey)
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
        resetPassword.addTextChangedListener(new EditChangedListener(resetPassword, savePasswordBtn, blueBtn, greyBtn, white, white));
    }

    @OnClick(R.id.back_to_get_veri_btn)
    public void backToGetVeriPage(View view) {
        Intent intent = new Intent();
        intent.setClass(ResetPasswordActivity.this, GetVerificationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.eye3)
    public void setRePasswordVisiable(View view) {
        ChangeVisiable.changeVisiable(Eye3, resetPassword);
    }
}
