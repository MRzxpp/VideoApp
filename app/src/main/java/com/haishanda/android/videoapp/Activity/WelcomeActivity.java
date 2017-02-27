package com.haishanda.android.videoapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.bean.UserBean;
import com.haishanda.android.videoapp.config.Constant;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.service.LoginService;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;


import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 首页 主要功能为完成一些基本模块的注册
 * 有注册和登录两个选项
 * Created by Zhongsz on 2016/10/18.
 */

public class WelcomeActivity extends Activity {
    @BindView(R.id.frameLayout)
    RelativeLayout loginAndRegisterBtns;

    @SuppressLint("StaticFieldLeak")
    public static WelcomeActivity instance;

    SharedPreferences preferences;
    private static final String TAG = "WelcomeActivity";

    TokenReceiver receiver;
    boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        instance = this;
        preferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
        //初始化环信
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setRequireAck(true);
        options.setRequireDeliveryAck(true);
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        //测试地址开关
        SharedPreferences preferences = getSharedPreferences("TEST_PATH", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("TEST_PATH_ON", true);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册接收是否通过token登录成功的token
        IntentFilter filter = new IntentFilter(Constant.ACTION_RECEIVE_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new TokenReceiver();
        registerReceiver(receiver, filter);
        isRegistered = true;
        Log.d(TAG, "tokenreceiver registered");
        Thread validateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (validateToken()) {
                    Intent intent = new Intent(WelcomeActivity.instance, LoginService.class);
                    intent.putExtra("validateTokenState", true);
                    startService(intent);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Animation animation = AnimationUtils.loadAnimation(instance, R.anim.gradually_appear);
                            loginAndRegisterBtns.startAnimation(animation);
                        }
                    });
                }
            }
        });
        validateThread.start();
        try {
            validateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRegistered) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "WelcomeActivity onDestroy!");
    }

    @OnClick({R.id.welcome_to_login, R.id.welcome_to_signup})
    public void skipToNextPage(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case (R.id.welcome_to_login): {
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                break;
            }
            case (R.id.welcome_to_signup): {
                intent.setClass(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                break;
            }
            default:
                break;
        }
    }

    private boolean validateToken() {
        String token = preferences.getString(Constant.USER_PREFERENCE_TOKEN, "");
        if (token.equals("")) {
            return false;
        } else {
            Call<SmartResult<UserBean>> call = ApiManage.getInstence().getUserApiService().validateToken(token);
            try {
                Response<SmartResult<UserBean>> response = call.execute();
                if (response.body().getCode() == 1) {
                    return true;
                } else {
                    Toast.makeText(this, response.body().getMsg(), Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public class TokenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean loginStatus = intent.getBooleanExtra("loginStatus", false);
            String loginMessage = intent.getStringExtra("loginMessage");
            boolean loginFromToken = intent.getBooleanExtra("loginFromToken", false);
            // 如果登录成功
            if (loginStatus && loginFromToken) {
                Intent nextIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(nextIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                // 结束该Activity
                WelcomeActivity.instance.finish();
                //注销广播接收器
                context.unregisterReceiver(receiver);
                isRegistered = false;
                Log.d(TAG, "tokenreceiver unregistered");
            }
            if (!loginStatus && loginFromToken) {
                Toast.makeText(context, loginMessage, Toast.LENGTH_LONG).show();
                Intent serviceIntent = new Intent(instance, LoginService.class);
                stopService(serviceIntent);
            }
        }
    }
}
