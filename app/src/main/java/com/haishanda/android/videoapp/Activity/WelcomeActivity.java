package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/18.
 */

public class WelcomeActivity extends Activity {
    @BindView(R.id.frameLayout)
    RelativeLayout loginAndRegisterBtns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
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

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.gradually_appear);
        boolean isLogin = EMClient.getInstance().isLoggedInBefore();
        if (isLogin) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            loginAndRegisterBtns.startAnimation(animation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().logout(true);
    }

    @OnClick({R.id.welcome_to_login, R.id.welcome_to_signup})
    public void skipToNextPage(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case (R.id.welcome_to_login): {
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case (R.id.welcome_to_signup): {
                intent.setClass(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }
}
