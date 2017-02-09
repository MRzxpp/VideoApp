package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Config.Constant;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Service.LoginService;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;
import com.haishanda.android.videoapp.Views.MaterialDialog;


import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录界面
 * Created by Zhongsz on 2016/10/9.
 */
public class LoginActivity extends Activity {
    @BindView(R.id.username_login_text)
    EditText username;
    @BindView(R.id.password_login_text)
    EditText password;
    @BindView(R.id.eye)
    TextView Eye;
    @BindView(R.id.clear3)
    ImageView clear3;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    public static final String TAG = "LoginActivity";
    private LoginMessageReceiver receiver;
    private MaterialDialog dialog;
    private boolean isRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        dialog = new MaterialDialog(this);
        dialog.setMessage("登录中…");
        clear3.setVisibility(View.INVISIBLE);
        loginBtn.setEnabled(false);
        password.addTextChangedListener(new ClearBtnListener(clear3, password));
        password.addTextChangedListener(new LoginListener(username, password, loginBtn, blueBtn, greyBtn, white, white));
    }

    @Override
    public void onResume() {
        super.onResume();
        //动态注册receiver
        Log.d(TAG, "onResume() excute");
        IntentFilter filter = new IntentFilter(Constant.ACTION_RECEIVE_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new LoginMessageReceiver();
        registerReceiver(receiver, filter);
        isRegistered = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() excute");
        if (isRegistered)
            unregisterReceiver(receiver);
    }

    @OnClick(R.id.fast_signup_btn)
    public void skipToSignupPage(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @OnClick(R.id.login_btn)
    public void loginService(View view) {
        if (username.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent msgIntent = new Intent(LoginActivity.this, LoginService.class);
            msgIntent.putExtra("username", username.getText().toString());
            msgIntent.putExtra("password", password.getText().toString());
            msgIntent.putExtra("validateTokenState", false);
            startService(msgIntent);
            dialog.show();
        }
    }

    @OnClick(R.id.back_to_index_btn)
    public void returnLastPage(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.forget_password_btn)
    public void skipToFindPasswordPage(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, GetVerificationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @OnClick(R.id.eye)
    public void setRePasswordVisiable(View view) {
        ChangeVisiable.changeVisiable(Eye, password);
    }

    @OnClick(R.id.clear3)
    public void clearPassword(View view) {
        password.setText("");
    }

    //接收广播类
    public class LoginMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean loginStatus = intent.getBooleanExtra("loginStatus", false);
            String loginMessage = intent.getStringExtra("loginMessage");
            boolean loginFromToken = intent.getBooleanExtra("loginFromToken", false);
            // 如果登录成功
            if (loginStatus && !loginFromToken) {
                // 启动Main Activity
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent nextIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(nextIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                // 结束该Activity
                WelcomeActivity.instance.finish();
                finish();
                //注销广播接收器
                context.unregisterReceiver(this);
                isRegistered = false;
            }
            if (!loginStatus && !loginFromToken) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(context, loginMessage, Toast.LENGTH_LONG).show();
                Intent serviceIntent = new Intent(LoginActivity.this, LoginService.class);
                stopService(serviceIntent);
            }
        }
    }
}
