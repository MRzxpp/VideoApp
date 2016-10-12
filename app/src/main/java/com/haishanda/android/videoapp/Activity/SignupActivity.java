package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.CountDownTimerUtil;

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
public class SignupActivity extends Activity {
    @BindView(R.id.phonenumber)
    EditText phoneNumber;
    @BindView(R.id.password_signup_text)
    EditText password;
    @BindView(R.id.repassword_signup_text)
    EditText rePassword;
    @BindView(R.id.message_text)
    EditText code;
    @BindView(R.id.eye1)
    ImageView Eye1;
    @BindView(R.id.eye2)
    ImageView Eye2;
    @BindView(R.id.get_fetch_code)
    Button getFetchCode;
    @BindView(R.id.clear1)
    ImageView clear1;
    @BindView(R.id.clear2)
    ImageView clear2;
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
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        clear1.setVisibility(View.INVISIBLE);
        clear2.setVisibility(View.INVISIBLE);
        password.addTextChangedListener(new ClearBtnListener(clear1, password));
        rePassword.addTextChangedListener(new ClearBtnListener(clear2, rePassword));
    }

    @OnClick(R.id.back_to_login_btn)
    public void returnLastPage(View view) {
        Intent intent = new Intent();
        intent.setClass(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.get_fetch_code)
    public void send_fetch_code(View view) {
        CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(getFetchCode, 120000, 1000, blueBtn, greyBtn, white, textGrey);
        countDownTimerUtil.start();
        String mobileNo = phoneNumber.getText().toString();
        ApiManage.getInstence().getUserApiService().getFetchCode(mobileNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i("info", "发送成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        Log.i("info", "正在发送");
                        Log.i("info", String.valueOf(smartResult.getCode()));
                    }

                });

    }


    @OnClick(R.id.signup_button)
    public void signupService(View view) {
        if (phoneNumber.getText().toString().length() != 11) {
            Toast.makeText(SignupActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().equals("") || rePassword.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "请输入密码", Toast.LENGTH_SHORT)
                    .show();
        } else if (!password.getText().toString().equals(rePassword.getText().toString())) {
            Toast.makeText(SignupActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT)
                    .show();
        } else if (code.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "请输入验证码", Toast.LENGTH_SHORT)
                    .show();
        } else {
//            RegisterBean registerBean = new RegisterBean();
//            registerBean.setCode(code.getText().toString());
//            registerBean.setMobileNo(phoneNumber.getText().toString());
//            registerBean.setPassword(password.getText().toString());
            ApiManage.getInstence().getUserApiService().signupAction(phoneNumber.getText().toString(), password.getText().toString(), code.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i("info", "注册成功");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            Log.i("info", String.valueOf(smartResult.getCode()));
                            Log.i("info", smartResult.getMsg());
                        }
                    });
        }
    }

    @OnClick(R.id.eye1)
    public void setPasswordVisiable(View view) {
        if (Eye1.getDrawable() != blueEye) {
            Eye1.setImageDrawable(blueEye);
            password.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            Eye1.setImageDrawable(greyEye);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @OnClick(R.id.eye2)
    public void setRePasswordVisiable(View view) {
        if (Eye2.getDrawable() != blueEye) {
            Eye2.setImageDrawable(blueEye);
            rePassword.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            Eye2.setImageDrawable(greyEye);
            rePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
