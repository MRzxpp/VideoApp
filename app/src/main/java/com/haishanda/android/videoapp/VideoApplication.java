package com.haishanda.android.videoapp;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

/**
 * Created by Zhongsz on 2016/10/12.
 */

public class VideoApplication extends Application {
    public static Context applicationContext;
//    private static VideoApplication instance;
    // login user name
//    public final String PREF_USERNAME = "username";

    /**
     * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
     */
//    public static String currentUserNick = "";
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setRequireAck(true);
        options.setRequireDeliveryAck(true);
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
//初始化
        EMClient.getInstance().init(VideoApplication.applicationContext, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }

//    public static VideoApplication getInstance() {
//        return instance;
//    }

    @Override
    protected void attachBaseContext(Context base) {

    }
}
