package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.haishanda.android.videoapp.Bean.FirstLogin;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.FirstLoginDao;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 首页 主要功能为完成一些基本模块的注册
 * 有注册和登录两个选项
 * Created by Zhongsz on 2016/10/18.
 */

public class WelcomeActivity extends Activity {
    @BindView(R.id.frameLayout)
    RelativeLayout loginAndRegisterBtns;

    public static WelcomeActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        instance = this;
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
        FirstLoginDao firstLoginDao = VideoApplication.getApplication().getDaoSession().getFirstLoginDao();
        QueryBuilder<FirstLogin> queryBuilder = firstLoginDao.queryBuilder();
        try {
            FirstLogin firstLogin = queryBuilder.unique();
            if (firstLogin != null) {
                if (firstLogin.getIsFirst() != 1) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    this.finish();
                }
            }
        } catch (DaoException e) {
            FirstLogin firstLogin = new FirstLogin(1);
            firstLoginDao.insertOrReplace(firstLogin);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.gradually_appear);
        loginAndRegisterBtns.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EMClient.getInstance().logout(true);
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
                intent.setClass(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                break;
            }
            default:
                break;
        }
    }
}
