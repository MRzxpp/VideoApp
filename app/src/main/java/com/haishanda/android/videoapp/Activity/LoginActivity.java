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
import com.haishanda.android.videoapp.Bean.UserBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.R;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/10/9.
 */
public class LoginActivity extends Activity {
    @BindView(R.id.username_login_text)
    EditText username;
    @BindView(R.id.password_login_text)
    EditText password;
    @BindView(R.id.eye)
    ImageView Eye;
    @BindView(R.id.clear3)
    ImageView clear3;
    @BindDrawable(R.drawable.eyeblue)
    Drawable blueEye;
    @BindDrawable(R.drawable.eye)
    Drawable greyEye;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        clear3.setVisibility(View.INVISIBLE);
        password.addTextChangedListener(new ClearBtnListener(clear3, password));
    }

    @OnClick(R.id.fast_signup_btn)
    public void skipToSignupPage(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_btn)
    public void loginService(View view) {
//        if (!username.getText().toString().equals("1234")) {
//            Toast.makeText(LoginActivity.this, "你好，该用户不存在，请先注册", Toast.LENGTH_SHORT)
//                    .show();
//        } else if (password.getText().toString().equals("")) {
//            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT)
//                    .show();
//        } else if (!password.getText().toString().equals("1234")) {
//            Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT)
//                    .show();
//        } else {
//            UserBean userBean = new UserBean();
//            userBean.setName(username.getText().toString());
//            userBean.setPassword(password.getText().toString());
        ApiManage.getInstence().getUserApiService().loginAction(username.getText().toString(), password.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i("info", "登录成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        Log.i("info", String.valueOf(smartResult.getCode()));
                    }
                });
    }


//    }

    @OnClick(R.id.back_to_index_btn)
    public void returnLastPage(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.forget_password_btn)
    public void skipToFindPasswordPage(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, GetVerificationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.eye)
    public void setRePasswordVisiable(View view) {
        if (Eye.getDrawable() != blueEye) {
            Eye.setImageDrawable(blueEye);
            password.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            Eye.setImageDrawable(greyEye);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @OnClick(R.id.clear3)
    public void clearPassword(View view) {
        password.setText("");
    }

}
