package com.haishanda.android.videoapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.utils.textwatcher.EditChangedWatcher;
import com.haishanda.android.videoapp.utils.textwatcher.LoginWatcher;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.CountDownTimerUtil;

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
 * 登录界面点击忘记密码后获取验证码
 * Created by Zhongsz on 2016/10/10.
 */

public class ForgetPasswordActivity extends FragmentActivity {
    @BindView(R.id.mobileNo_get_veri_text)
    EditText phoneNum;
    @BindView(R.id.fetch_code_getveri_input)
    EditText fetchCode;
    @BindView(R.id.reset_password_btn)
    Button toResetPwdPage;
    @BindView(R.id.get_code_btn)
    Button getCodeBtn;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    private String mobileNo = "0";
    private final String TAG = "忘记密码";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        toResetPwdPage.setEnabled(false);
        getCodeBtn.setEnabled(false);
        fetchCode.addTextChangedListener(new LoginWatcher(fetchCode, phoneNum, toResetPwdPage, blueBtn, greyBtn, white, white));
        phoneNum.addTextChangedListener(new EditChangedWatcher(phoneNum, getCodeBtn, blueBtn, greyBtn, white, white));
    }

    @OnClick(R.id.reset_password_btn)
    public void skipToResetPasswordPage(View view) {
        ApiManage.getInstence().getUserApiService().validateResetCode(this.mobileNo, fetchCode.getText().toString())
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
                            Log.i(TAG, "validate success");
                            Bundle extra = new Bundle();
                            extra.putString("moblieNo", mobileNo);
                            ResetPasswordUnloginedFragment resetPasswordUnloginedFragment = new ResetPasswordUnloginedFragment();
                            resetPasswordUnloginedFragment.setArguments(extra);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
                            fragmentTransaction.replace(R.id.forget_password_layout, resetPasswordUnloginedFragment);
                            fragmentTransaction.commit();
                        } else {
                            Log.i(TAG, "validate failed");
                            Toast.makeText(getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "验证码输入错误", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });

    }

    @OnClick(R.id.back_to_login_btn2)
    public void returnLastPage(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.get_code_btn)
    public void send_fetch_code(View view) {
        CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(getCodeBtn, 120000, 1000, blueBtn, greyBtn, white, white);
        countDownTimerUtil.start();
        Pattern phoneNumPattern = Pattern.compile("^[1][3578][0-9]{9}$");
        Matcher phoneNumMatcher = phoneNumPattern.matcher(phoneNum.getText().toString());
        String mobileNo = phoneNum.getText().toString();
        this.mobileNo = mobileNo;
        if (!phoneNumMatcher.matches()) {
            Toast.makeText(getApplicationContext(), "手机号格式输入不正确", Toast.LENGTH_SHORT).show();
        } else {
            ApiManage.getInstence().getUserApiService().getResetCode(mobileNo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i("info", "发送结束");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            Log.i("info", "正在发送");
                            if (smartResult.getCode() != 1) {
                                Toast.makeText(getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "发送失败，请检查手机号是否输入正确", Toast.LENGTH_LONG).show();
                                Log.i("info", String.valueOf(smartResult.getCode()));
                            } else {
                                Toast.makeText(getApplicationContext(), "发送成功，请查收，请勿泄露验证码", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.meet_problem)
    public void skipToProblemPage(View view) {
        Intent intent = new Intent(ForgetPasswordActivity.this, ProblemActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
}
