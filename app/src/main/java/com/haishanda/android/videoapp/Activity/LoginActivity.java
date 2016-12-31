package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.AlarmNum;
import com.haishanda.android.videoapp.Bean.FirstLogin;
import com.haishanda.android.videoapp.Bean.LoginMessage;
import com.haishanda.android.videoapp.Bean.UserBean;
import com.haishanda.android.videoapp.Bean.UserMessageBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;
import com.haishanda.android.videoapp.Utils.NotificationUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.AlarmNumDao;
import com.haishanda.android.videoapp.greendao.gen.AlarmVoBeanDao;
import com.haishanda.android.videoapp.greendao.gen.FirstLoginDao;
import com.haishanda.android.videoapp.greendao.gen.LastIdDao;
import com.haishanda.android.videoapp.greendao.gen.LoginMessageDao;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.haishanda.android.videoapp.greendao.gen.UserMessageBeanDao;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        clear3.setVisibility(View.INVISIBLE);
        loginBtn.setEnabled(false);
        password.addTextChangedListener(new ClearBtnListener(clear3, password));
        password.addTextChangedListener(new LoginListener(username, password, loginBtn, blueBtn, greyBtn, white, white));
        Thread thread = new Thread(new LoginThread());
        thread.start();

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
            ApiManage.getInstence().getUserApiService().loginAction(username.getText().toString(), password.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult<UserBean>>() {
                        @Override
                        public void onCompleted() {
                            Log.i("info", "登录结束");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult<UserBean> smartResult) {
                            if (smartResult.getCode() != 1) {
                                Toast.makeText(LoginActivity.this, smartResult.getMsg(), Toast.LENGTH_SHORT).show();
                            } else {
                                FirstLoginDao firstLoginDao = VideoApplication.getApplication().getDaoSession().getFirstLoginDao();
                                FirstLogin firstLogin = new FirstLogin(0);
                                firstLoginDao.deleteAll();
                                firstLoginDao.insertOrReplace(firstLogin);
                                LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
                                LoginMessage loginMessage = new LoginMessage(username.getText().toString(), password.getText().toString(), smartResult.getData().getId());
                                loginMessageDao.deleteAll();
                                loginMessageDao.insert(loginMessage);
                                UserMessageBeanDao userMessageBeanDao = VideoApplication.getApplication().getDaoSession().getUserMessageBeanDao();
                                UserMessageBean userMessageBean = new UserMessageBean(smartResult.getData().getName(), smartResult.getData().getPortrait(), smartResult.getData().getId());
                                userMessageBeanDao.insertOrReplace(userMessageBean);
                                //token inject
                                VideoApplication.getApplication().setToken(smartResult.getData().getToken());
                                //emclient login
                                EMClient.getInstance().login("appmonitor_" + String.valueOf(smartResult.getData().getId()), username.getText().toString(), new EMCallBack() {//回调
                                    @Override
                                    public void onSuccess() {
                                        EMClient.getInstance().groupManager().loadAllGroups();
                                        EMClient.getInstance().chatManager().loadAllConversations();
                                        Log.d("main", "登录聊天服务器成功！");
                                        EMMessageListener msgListener = new EMMessageListener() {

                                            @Override
                                            public void onMessageReceived(List<EMMessage> messages) {
                                                int i = (int) (1 + Math.random() * 65535);
                                                //收到消息
                                                Log.d("receive message", "success");
                                                NotificationUtil notificationUtil = new NotificationUtil(LoginActivity.this);
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                notificationManager.notify(i, notificationUtil.initNotify(messages.get(0).getBody().toString()).build());
                                                try {
                                                    if (messages.get(0).getStringAttribute("type").equals("alarm")) {
                                                        AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
                                                        QueryBuilder<AlarmNum> queryBuilder = alarmNumDao.queryBuilder();
                                                        AlarmNum alarmNum = queryBuilder.unique();
                                                        AlarmNum newAlarnNum = new AlarmNum(alarmNum.getAlarmNum() + 1);
                                                        alarmNumDao.deleteAll();
                                                        alarmNumDao.insert(newAlarnNum);
                                                        MainActivity mainActivity = MainActivity.instance;
                                                        mainActivity.refresh();
                                                    }
                                                } catch (HyphenateException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCmdMessageReceived(List<EMMessage> messages) {
                                                //收到透传消息
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
                                        EMClient.getInstance().chatManager().addMessageListener(msgListener);
                                        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

                                        Intent intent = new Intent();
                                        intent.setClass(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                                        LoginActivity.this.finish();
                                        WelcomeActivity.instance.finish();
                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {

                                    }

                                    @Override
                                    public void onError(int code, String message) {
                                        Log.d("main", "登录聊天服务器失败！");
                                    }
                                });
                            }
                            Log.i("info", String.valueOf(smartResult.getCode()));
                            if (smartResult.getMsg() != null) {
                                Log.i("info", smartResult.getMsg());
                            }
                        }
                    });
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

    class LoginThread implements Runnable {

        @Override
        public void run() {
            Looper.prepare();
            loginWithExistMessage();
        }
    }

    public boolean loginWithExistMessage() {
        LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
        QueryBuilder<LoginMessage> queryBuilder = loginMessageDao.queryBuilder();
        LoginMessage loginMessage;
        try {
            loginMessage = queryBuilder.uniqueOrThrow();
        } catch (DaoException e) {
            return false;
        }
        if (loginMessage == null) {
            loginMessage = new LoginMessage("1", "1", -1);
        }
        String username = loginMessage.getUsername();
        String password = loginMessage.getPassword();
//        the two lines are bugs and i don't know why yet :(
//        this.username.setText(username);
//        this.password.setText(password);
        final boolean[] isLogined = {false};
        Call<SmartResult<UserBean>> call = ApiManage.getInstence().getUserApiService().loginActionCopy(username, password);
        try {
            Response<SmartResult<UserBean>> response = call.execute();
            if (response.body().getCode() == 1) {
                FirstLoginDao firstLoginDao = VideoApplication.getApplication().getDaoSession().getFirstLoginDao();
                FirstLogin firstLogin = new FirstLogin(0);
                firstLoginDao.deleteAll();
                firstLoginDao.insertOrReplace(firstLogin);
                UserMessageBeanDao userMessageBeanDao = VideoApplication.getApplication().getDaoSession().getUserMessageBeanDao();
                UserMessageBean userMessageBean = new UserMessageBean(response.body().getData().getName(), response.body().getData().getPortrait(), response.body().getData().getId());
                userMessageBeanDao.insertOrReplace(userMessageBean);
                isLogined[0] = true;
                //token inject
                VideoApplication.getApplication().setToken(response.body().getData().getToken());
                //emclient login
                EMClient.getInstance().login("appmonitor_" + String.valueOf(response.body().getData().getId()), username, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d("main", "登录聊天服务器成功！");

                        EMMessageListener msgListener = new EMMessageListener() {

                            @Override
                            public void onMessageReceived(List<EMMessage> messages) {
                                int i = (int) (1 + Math.random() * 65535);
                                //收到消息
                                Log.d("receive message", "success");
                                NotificationUtil notificationUtil = new NotificationUtil(LoginActivity.this);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(i, notificationUtil.initNotify(messages.get(0).getBody().toString()).build());
                                try {
                                    if (messages.get(0).getStringAttribute("type").equals("alarm")) {
                                        AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
                                        QueryBuilder<AlarmNum> queryBuilder = alarmNumDao.queryBuilder();
                                        AlarmNum alarmNum = queryBuilder.unique();
                                        AlarmNum newAlarnNum = new AlarmNum(alarmNum.getAlarmNum() + 1);
                                        alarmNumDao.deleteAll();
                                        alarmNumDao.insert(newAlarnNum);
                                        MainActivity mainActivity = MainActivity.instance;
                                        mainActivity.refresh();
                                    }
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
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
                        EMClient.getInstance().chatManager().addMessageListener(msgListener);
                        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                        LoginActivity.this.finish();
                        WelcomeActivity.instance.finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d("main", "登录聊天服务器失败！");
                    }
                });
            }
            Log.i("info", String.valueOf(response.body().getCode()));
            if (response.body().getMsg() != null) {
                Log.i("info", response.body().getMsg());
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "连接服务器超时", Toast.LENGTH_LONG).show();
        } catch (ConnectException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "系统错误", Toast.LENGTH_LONG).show();
        }
        return isLogined[0];
    }


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        Toast.makeText(getApplicationContext(), "账户已被移除，请联系经销商", Toast.LENGTH_LONG).show();
                        logoutAndDeleteLoginMessage();
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(getBaseContext(), "账户在其他地方登录", Toast.LENGTH_LONG).show();
                        logoutAndDeleteLoginMessage();
                    } else {
                        if (NetUtils.hasNetwork(LoginActivity.this)) {
                            Toast.makeText(getApplicationContext(), "连接环信服务器失败", Toast.LENGTH_LONG).show();
                            EMClient.getInstance().logout(true);
                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        }
                        //连接不到聊天服务器
                        else {
                            Toast.makeText(getApplicationContext(), "当前网络不可用", Toast.LENGTH_LONG).show();
                            EMClient.getInstance().logout(true);
                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        }
                        //当前网络不可用，请检查网络设置
                    }
                }
            });
        }
    }

    private void logoutAndDeleteLoginMessage() {
        //监控数目重置
        AlarmVoBeanDao alarmVoBeanDao = VideoApplication.getApplication().getDaoSession().getAlarmVoBeanDao();
        alarmVoBeanDao.deleteAll();
        LastIdDao lastIdDao = VideoApplication.getApplication().getDaoSession().getLastIdDao();
        lastIdDao.deleteAll();
        //清除监控配置信息
        MonitorConfigBeanDao monitorConfigBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorConfigBeanDao();
        monitorConfigBeanDao.deleteAll();
        //报警数目归零
        AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
        alarmNumDao.deleteAll();
        //清除登录信息
        LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
        loginMessageDao.deleteAll();
        //重置VideoApplication
        VideoApplication.getApplication().setCurrentBoatName(null);
        VideoApplication.getApplication().setCurrentMachineId(-1);
        //重置是否第一次登录
        FirstLoginDao firstLoginDao = VideoApplication.getApplication().getDaoSession().getFirstLoginDao();
        FirstLogin firstLogin = new FirstLogin(1);
        firstLoginDao.deleteAll();
        firstLoginDao.insertOrReplace(firstLogin);
        Thread emThread = new Thread(new EMThread());
        emThread.start();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    class EMThread implements Runnable {
        @Override
        public void run() {
            EMClient.getInstance().logout(true);
        }
    }

}
