package com.haishanda.android.videoapp.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.haishanda.android.videoapp.Activity.MainActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.AlarmNum;
import com.haishanda.android.videoapp.Bean.UserBean;
import com.haishanda.android.videoapp.Bean.UserMessageBean;
import com.haishanda.android.videoapp.Config.Constant;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.NotificationUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.AlarmNumDao;
import com.haishanda.android.videoapp.greendao.gen.UserMessageBeanDao;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 登录服务
 * Created by Zhongsz on 2016/12/16.
 */

public class LoginService extends Service {

    public static final String ACTION_RECEIVE_TIMER = "com.haishanda.android.videoapp.Service.LoginService.RECEIVE_TIMER";
    public static final String SUCCESS = "登录成功";
    public static final String EMERROR_CONFLICT = "账户在其他地方登录";
    public static final String EMERROR_DISCONNECT = "连接环信服务器失败";
    public static final String EMERROR_CLIENT_REMOVED = "账户已被移除，请联系经销商";
    public static final String EMERROR_CHAT_FAILED = "登录聊天服务器失败";
    public static final String NETERROR_ENABLED = "当前网络不可用";
    public static final String HSDERROR_TIMEOUT = "连接服务器超时";
    public static final String HSDERROR_NETERROR = "连接服务器失败";
    public static final String SYSTEM_ERROR = "系统错误";

    private final static String TAG = "LoginService";
    TimerTaskReceiver receiver;
    EMMessageListener emMessageListener;
    MyConnectionListener connectionListener;

    public static Timer getWarningTimer() {
        return warningTimer;
    }

    public void setWarningTimer(Timer warningTimer) {
        LoginService.warningTimer = warningTimer;
    }

    static Timer warningTimer;
    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        IntentFilter filter = new IntentFilter(ACTION_RECEIVE_TIMER);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new TimerTaskReceiver();
        preferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
        registerReceiver(receiver, filter);
        if (emMessageListener == null) {
            emMessageListener = new EMMessageListener() {
                @Override
                public void onMessageReceived(List<EMMessage> messages) {
                    int i = (int) (1 + Math.random() * 65535);
                    //收到消息
                    Log.d("receive message", "success");
                    NotificationUtil notificationUtil = new NotificationUtil(LoginService.this);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(i, notificationUtil.initNotify(messages.get(0).getBody().toString()).build());
                    try {
                        if (messages.get(0).getStringAttribute("type").equals("alarm")) {
                            //如果收到的消息是船舶报警则唤醒振动
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(ACTION_RECEIVE_TIMER);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra("message", messages.get(0).getBody().toString());
                            sendBroadcast(broadcastIntent);
                            //报警数目增加
                            AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
                            QueryBuilder<AlarmNum> queryBuilder = alarmNumDao.queryBuilder();
                            AlarmNum alarmNum = queryBuilder.unique();
                            AlarmNum newAlarnNum = new AlarmNum(alarmNum.getAlarmNum() + 1);
                            alarmNumDao.deleteAll();
                            alarmNumDao.insert(newAlarnNum);
                            //小红点更新
                            MainActivity.instance.refresh();
                        }
                    } catch (HyphenateException e) {
                        Log.d(TAG, "环信消息出现错误" + e.toString());
                    }
                }

                @Override
                public void onCmdMessageReceived(List<EMMessage> messages) {
                    //收到透传消息
                    Log.d("receive message", "success");
                }

                @Override
                public void onMessageRead(List<EMMessage> list) {

                }

                @Override
                public void onMessageDelivered(List<EMMessage> list) {

                }

                @Override
                public void onMessageChanged(EMMessage message, Object change) {
                    //消息状态变动
                }
            };
        }
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        final Notification noti = new Notification.Builder(this)
                .setContentTitle("渔船监控")
                .setContentText("正在保护您的渔船")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        boolean validateTokenState = intent.getBooleanExtra("validateTokenState", false);
        //验证token的方式登录
        if (validateTokenState) {
            startForeground(12346, noti);
            connectionListener = new MyConnectionListener(true);
            SharedPreferences sharedPreferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
            int userId = sharedPreferences.getInt(Constant.USER_PREFERENCE_ID, -1);
            String savedUsername = sharedPreferences.getString(Constant.USER_PREFERENCE_USERNAME, "");
            EMClient.getInstance().login("appmonitor_" + String.valueOf(userId), savedUsername, new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    Log.d(TAG, "登录聊天服务器成功！");
                    sendLoginedMsg(true, SUCCESS, true);
                    if (emMessageListener != null) {
                        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
                    }
                    if (connectionListener != null) {
                        EMClient.getInstance().addConnectionListener(connectionListener);
                    }
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, String message) {
                    Log.d(TAG, "登录聊天服务器失败！" + message);
                    sendLoginedMsg(false, EMERROR_CHAT_FAILED, true);
                }
            });
        } else {
            connectionListener = new MyConnectionListener(false);
            //点击登录页面的按钮登录
            final String username = intent.getStringExtra("username");
            final String password = intent.getStringExtra("password");
            final Call<SmartResult<UserBean>> call = ApiManage.getInstence().getUserApiService().loginActionCopy(username, password);
            Thread netThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<SmartResult<UserBean>> response = call.execute();
                        if (response.body().getCode() == 1) {
                            startForeground(12346, noti);
//                            FirstLoginDao firstLoginDao = VideoApplication.getApplication().getDaoSession().getFirstLoginDao();
//                            FirstLogin firstLogin = new FirstLogin(0);
//                            firstLoginDao.deleteAll();
//                            firstLoginDao.insertOrReplace(firstLogin);
                            UserMessageBeanDao userMessageBeanDao = VideoApplication.getApplication().getDaoSession().getUserMessageBeanDao();
                            UserMessageBean userMessageBean = new UserMessageBean(response.body().getData().getName(), response.body().getData().getPortrait(), response.body().getData().getId());
                            userMessageBeanDao.insertOrReplace(userMessageBean);
                            //token inject
                            SharedPreferences preferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constant.USER_PREFERENCE_TOKEN, response.body().getData().getToken());
                            editor.putString(Constant.USER_PREFERENCE_USERNAME, username);
                            editor.putInt(Constant.USER_PREFERENCE_ID, response.body().getData().getId());
                            editor.apply();
                            //emclient login
                            EMClient.getInstance().login("appmonitor_" + String.valueOf(response.body().getData().getId()), username, new EMCallBack() {//回调
                                @Override
                                public void onSuccess() {
                                    EMClient.getInstance().groupManager().loadAllGroups();
                                    EMClient.getInstance().chatManager().loadAllConversations();
                                    Log.d(TAG, "登录聊天服务器成功！");
                                    sendLoginedMsg(true, SUCCESS, false);
                                    if (emMessageListener != null) {
                                        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
                                    }
                                    if (connectionListener != null) {
                                        EMClient.getInstance().addConnectionListener(connectionListener);
                                    }
                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }

                                @Override
                                public void onError(int code, String message) {
                                    Log.d(TAG, "登录聊天服务器失败！" + message);
                                    sendLoginedMsg(false, EMERROR_CHAT_FAILED, false);
                                }
                            });
                        } else {
                            sendLoginedMsg(false, response.body().getMsg(), false);
                            Log.i(TAG, String.valueOf(response.body().getCode()));
                            if (response.body().getMsg() != null) {
                                Log.i(TAG, response.body().getMsg());
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        Log.e(TAG, e.toString());
                        sendLoginedMsg(false, HSDERROR_TIMEOUT, false);
                    } catch (ConnectException e) {
                        Log.e(TAG, e.toString());
                        sendLoginedMsg(false, HSDERROR_NETERROR, false);
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                        sendLoginedMsg(false, SYSTEM_ERROR, false);
                    }
                }
            });
            netThread.start();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
        unregisterReceiver(receiver);
        EMClient.getInstance().removeConnectionListener(connectionListener);
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
    }

    public class TimerTaskReceiver extends BroadcastReceiver {

        private Timer timer;
        private TimerTask timerTask;

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences warningPreferences = getSharedPreferences(Constant.WARNING_CONFIG, MODE_PRIVATE);
            boolean isTimerTaskOn = true;
            boolean isVibratorOn = true;
            boolean isVoiceOn = true;
            if (warningPreferences.contains(Constant.WARNING_CONFIG_TIMER)) {
                isTimerTaskOn = warningPreferences.getBoolean(Constant.WARNING_CONFIG_TIMER, true);
            }
            if (warningPreferences.contains(Constant.WARNING_CONFIG_VIBRATE)) {
                isVibratorOn = warningPreferences.getBoolean(Constant.WARNING_CONFIG_VIBRATE, true);
            }
            if (warningPreferences.contains(Constant.WARNING_CONFIG_VOICE)) {
                isVoiceOn = warningPreferences.getBoolean(Constant.WARNING_CONFIG_VOICE, true);
            }
            if (isTimerTaskOn) {
                if (timer == null) {
                    timer = new Timer();
                    final boolean finalIsVibratorOn = isVibratorOn;
                    final boolean finalIsVoiceOn = isVoiceOn;
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            //唤醒屏幕
                            Log.d(TAG, "唤醒屏幕");
                            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "monitorWake");
                            wakeLock.acquire();
                            wakeLock.release();
                            if (finalIsVibratorOn) {
                                Log.d(TAG, "振动提醒");
                                //振动器实例化
                                Vibrator mVibrator;
                                mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                                //等待100ms后，按数组所给数值间隔震动；其后为重复次数，-1为不重复，0一直震动
                                mVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
                                mVibrator.vibrate(3000);
                                mVibrator.cancel();
                            }
                            if (finalIsVoiceOn) {
                                //声音提醒
                                Log.d(TAG, "声音提醒");
                                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ding);
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.seekTo(0);
                                    }
                                });
                                mediaPlayer.setVolume(0.30f, 0.30f);
                                mediaPlayer.start();
                            }
                        }
                    };
                    int timeSpan = warningPreferences.getInt(Constant.WARNING_CONFIG_TIME_SPAN, 15);
                    timer.schedule(timerTask, 0, timeSpan * 60 * 1000);
                    if (getWarningTimer() == null) {
                        setWarningTimer(timer);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLoginedMsg(boolean loginStatus, String loginMessage, boolean loginFromToken) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constant.ACTION_RECEIVE_MSG);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("loginStatus", loginStatus);
        broadcastIntent.putExtra("loginMessage", loginMessage);
        broadcastIntent.putExtra("loginFromToken", loginFromToken);
        sendBroadcast(broadcastIntent);
    }

    private class MyConnectionListener implements EMConnectionListener {

        private boolean loginFromToken;

        MyConnectionListener(boolean loginFromToken) {
            this.loginFromToken = loginFromToken;
        }

        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            if (error == EMError.USER_REMOVED) {
                sendLoginedMsg(false, EMERROR_CLIENT_REMOVED, loginFromToken);
                // 显示帐号已经被移除
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                // 显示帐号在其他设备登录
                sendLoginedMsg(false, EMERROR_CONFLICT, loginFromToken);
            } else {
                if (NetUtils.hasNetwork(LoginService.this)) {
                    //连接不到聊天服务器
                    sendLoginedMsg(false, EMERROR_DISCONNECT, loginFromToken);
                    EMClient.getInstance().logout(true);
                } else {
                    //当前网络不可用，请检查网络设置
                    sendLoginedMsg(false, NETERROR_ENABLED, loginFromToken);
                    EMClient.getInstance().logout(true);
                }
            }
        }
    }

}
