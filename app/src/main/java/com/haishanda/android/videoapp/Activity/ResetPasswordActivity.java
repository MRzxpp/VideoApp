package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.EditChangedListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private String mobileNo;
    private String TAG = "保存新密码";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        ButterKnife.bind(this);
        Bundle extra = getIntent().getExtras();
        mobileNo = extra.getString("mobileNo");
        savePasswordBtn.setEnabled(false);
        resetPassword.addTextChangedListener(new EditChangedListener(resetPassword, savePasswordBtn, blueBtn, greyBtn, white, white));
    }

    @OnClick(R.id.back_to_get_veri_btn)
    public void backToGetVeriPage(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.eye3)
    public void setRePasswordVisiable(View view) {
        ChangeVisiable.changeVisiable(Eye3, resetPassword);
    }

    @OnClick(R.id.save_new_password_btn)
    public void saveNewPassword(View view) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$");
        Matcher passwordMatcher = passwordPattern.matcher(resetPassword.getText().toString());
        if (resetPassword.getText().toString().length() < 6) {
            Toast.makeText(ResetPasswordActivity.this, "密码长度不足6位，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (resetPassword.getText().toString().length() > 20) {
            Toast.makeText(ResetPasswordActivity.this, "密码长度过长，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (!passwordMatcher.matches()) {
            Toast.makeText(ResetPasswordActivity.this, "密码过于简单，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else {
            ApiManage.getInstence().getUserApiService().forgetPassword(mobileNo, resetPassword.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i(TAG, "completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "error");
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                Log.i(TAG, "reset success");
                                Toast.makeText(getApplicationContext(), "修改成功，请重新登录！", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                                ResetPasswordActivity.this.finish();
                            } else {
                                Log.i(TAG, "reset failed");
                                Toast.makeText(getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "修改失败", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }
    }
}
