package com.haishanda.android.videoapp.Config;

import android.support.annotation.NonNull;
import android.view.View;

import butterknife.ButterKnife;

public class Constant {
    public final static String SERVER_HOME = "http://114.55.28.239:8082";

    public final static String WARNING_CONFIG = "WARNING_CONFIG";
    public final static String WARNING_CONFIG_TIMER = "timerTask";
    public final static String WARNING_CONFIG_VOICE = "vibrate";
    public final static String WARNING_CONFIG_VIBRATE = "makeVoice";
    public final static String WARNING_CONFIG_TIME_SPAN = "timeSpan";
    public final static String WARNING_CONFIG_SMS = "sms";

    public final static String ALARM_MESSAGE = "ALARM_MESSAGE";
    public final static String ALARM_MESSAGE_NUMBER = "alarmNumber";
    public final static String ALARM_MESSAGE_LAST_ID = "lastId";

    public final static String USER_PREFERENCE = "USER_PREFERENCE";
    public final static String USER_PREFERENCE_TOKEN = "token";
    public final static String USER_PREFERENCE_USERNAME = "username";
    public final static String USER_PREFERENCE_ID = "userId";

    public static final String SUCCESS = "登录成功";
    public static final String EMERROR_CONFLICT = "账户在其他地方登录";
    public static final String EMERROR_DISCONNECT = "连接环信服务器失败";
    public static final String EMERROR_CLIENT_REMOVED = "账户已被强制下线，请联系经销商";
    public static final String EMERROR_CHAT_FAILED = "登录聊天服务器失败";
    public static final String NETERROR_ENABLED = "当前网络不可用";
    public static final String HSDERROR_TIMEOUT = "连接服务器超时";
    public static final String HSDERROR_NETERROR = "连接服务器失败";
    public static final String SYSTEM_ERROR = "系统错误";

    public static final String ACTION_RECEIVE_MSG = "com.haishanda.android.videoapp.Service.LoginService.RECEIVE_MESSAGE";

    //ButterKnife工具类，改变ButterKnife绑定的View的可用性
    public static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(@NonNull View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };

}
