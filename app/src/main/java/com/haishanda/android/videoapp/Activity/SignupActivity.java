package com.haishanda.android.videoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.utils.textwatcher.ClearBtnWatcher;
import com.haishanda.android.videoapp.utils.textwatcher.SignUpWatcher;
import com.haishanda.android.videoapp.utils.textwatcher.SignupCodeWatcher;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.ViewUtil;
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
 * signup
 * Created by Zhongsz on 2016/10/9.
 */
public class SignUpActivity extends Activity {
    @BindView(R.id.phonenumber)
    EditText phoneNumber;
    @BindView(R.id.password_signup_text)
    EditText password;
    @BindView(R.id.repassword_signup_text)
    EditText rePassword;
    @BindView(R.id.message_text)
    EditText code;
    @BindView(R.id.eye1)
    TextView Eye1;
    @BindView(R.id.eye2)
    TextView Eye2;
    @BindView(R.id.get_fetch_code)
    Button getFetchCode;
    @BindView(R.id.signup_button)
    Button signUpBtn;
    @BindView(R.id.clear1)
    ImageView clear1;
    @BindView(R.id.clear2)
    ImageView clear2;
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
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        getFetchCode.setEnabled(false);
        signUpBtn.setEnabled(false);
        clear1.setVisibility(View.INVISIBLE);
        clear2.setVisibility(View.INVISIBLE);
        password.addTextChangedListener(new ClearBtnWatcher(clear1, password));
        rePassword.addTextChangedListener(new ClearBtnWatcher(clear2, rePassword));
        rePassword.addTextChangedListener(new SignupCodeWatcher(phoneNumber, password, rePassword, getFetchCode, blueBtn, greyBtn, white, white));
        code.addTextChangedListener(new SignUpWatcher(phoneNumber, password, rePassword, code, signUpBtn, blueBtn, greyBtn, white, white));
    }

    @OnClick(R.id.back_to_login_btn)
    public void returnLastPage(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.get_fetch_code)
    public void send_fetch_code(View view) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$");
        Matcher passwordMatcher = passwordPattern.matcher(password.getText().toString());
        Pattern phoneNumPattern = Pattern.compile("^[1][3578][0-9]{9}$");
        Matcher phoneNumMatcher = phoneNumPattern.matcher(phoneNumber.getText().toString());
        if (phoneNumber.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "请输入手机号", Toast.LENGTH_SHORT)
                    .show();
        } else if (!phoneNumMatcher.matches()) {
            Toast.makeText(SignUpActivity.this, "手机号格式不正确，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().equals("") || rePassword.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "请输入密码", Toast.LENGTH_SHORT)
                    .show();
        } else if (!password.getText().toString().equals(rePassword.getText().toString())) {
            Toast.makeText(SignUpActivity.this, "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(SignUpActivity.this, "密码长度不足6位，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().length() > 20) {
            Toast.makeText(SignUpActivity.this, "密码长度过长，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (!passwordMatcher.matches()) {
            Toast.makeText(SignUpActivity.this, "密码过于简单，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else {
            CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(getFetchCode, 120000, 1000, blueBtn, greyBtn, white, white);
            countDownTimerUtil.start();
            String mobileNo = phoneNumber.getText().toString();
            ApiManage.getInstence().getUserApiService().getFetchCode(mobileNo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i("获取验证码", "发送成功");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                Toast.makeText(getApplicationContext(), "已发送", Toast.LENGTH_LONG).show();
                                Log.d("获取验证码", "正在发送");
                            } else {
                                Toast.makeText(getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "发送失败", Toast.LENGTH_LONG).show();
                                Log.d("获取验证码", "发送失败");
                                Log.d("获取验证码", String.valueOf(smartResult.getCode()));
                            }

                        }
                    });
        }
    }


    @OnClick(R.id.signup_button)
    public void signupService(View view) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$");
        Matcher passwordMatcher = passwordPattern.matcher(password.getText().toString());
        Pattern phoneNumPattern = Pattern.compile("^[1][3578][0-9]{9}$");
        Matcher phoneNumMatcher = phoneNumPattern.matcher(phoneNumber.getText().toString());
        if (phoneNumber.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "请输入手机号", Toast.LENGTH_SHORT)
                    .show();
        } else if (!phoneNumMatcher.matches()) {
            Toast.makeText(SignUpActivity.this, "手机号格式不正确，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().equals("") || rePassword.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "请输入密码", Toast.LENGTH_SHORT)
                    .show();
        } else if (!password.getText().toString().equals(rePassword.getText().toString())) {
            Toast.makeText(SignUpActivity.this, "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(SignUpActivity.this, "密码长度不足6位，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().length() > 20) {
            Toast.makeText(SignUpActivity.this, "密码长度过长，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (!passwordMatcher.matches()) {
            Toast.makeText(SignUpActivity.this, "密码过于简单，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (code.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "请输入验证码", Toast.LENGTH_SHORT)
                    .show();
        } else {
            ApiManage.getInstence().getUserApiService().signupAction(phoneNumber.getText().toString(), password.getText().toString(), code.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i("info", "注册完成");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            Log.i("info", String.valueOf(smartResult.getCode()));
                            Log.i("info", smartResult.getMsg());
                            if (smartResult.getCode() != 1) {
                                Toast.makeText(SignUpActivity.this, smartResult.getMsg() != null ? smartResult.getMsg() : "注册失败，请重试！", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                                SignUpActivity.this.finish();
                            }
                        }
                    });
        }
    }

    @OnClick({R.id.eye1, R.id.eye2})
    public void setPasswordVisiable(View view) {
        switch (view.getId()) {
            case R.id.eye1: {
                ViewUtil.changeVisiable(Eye1, password);
                break;
            }
            case R.id.eye2: {
                ViewUtil.changeVisiable(Eye2, rePassword);
                break;
            }
            default:
                break;
        }
    }

    @OnClick({R.id.clear1, R.id.clear2})
    public void clearPassword(View view) {
        switch (view.getId()) {
            case R.id.clear1: {
                password.setText("");
                break;
            }
            case R.id.clear2: {
                rePassword.setText("");
                break;
            }
            default:
                break;
        }
    }


}
