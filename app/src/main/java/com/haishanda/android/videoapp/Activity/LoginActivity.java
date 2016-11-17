package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.UserBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;
import com.haishanda.android.videoapp.Utils.NotificationUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import butterknife.BindColor;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        clear3.setVisibility(View.INVISIBLE);
        loginBtn.setEnabled(false);
        password.addTextChangedListener(new ClearBtnListener(clear3, password));
        password.addTextChangedListener(new LoginListener(username, password, loginBtn, blueBtn, greyBtn, white, white));

    }

    @OnClick(R.id.fast_signup_btn)
    public void skipToSignupPage(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
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
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult<UserBean> smartResult) {
                            if (smartResult.getCode() != 1) {
                                Toast.makeText(LoginActivity.this, smartResult.getMsg(), Toast.LENGTH_SHORT).show();
                            } else {
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
                                                //收到消息
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                NotificationUtil notificationUtil = new NotificationUtil(LoginActivity.this);
                                                notificationManager.notify(1, notificationUtil.initNotify().build());
                                            }

                                            @Override
                                            public void onCmdMessageReceived(List<EMMessage> messages) {
                                                //收到透传消息
                                            }

                                            @Override
                                            public void onMessageReadAckReceived(List<EMMessage> messages) {
                                                //收到已读回执
                                            }

                                            @Override
                                            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                                                //收到已送达回执
                                            }

                                            @Override
                                            public void onMessageChanged(EMMessage message, Object change) {
                                                //消息状态变动
                                            }
                                        };
                                        EMClient.getInstance().chatManager().addMessageListener(msgListener);

                                        Intent intent = new Intent();
                                        intent.setClass(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
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


//    }

    @OnClick(R.id.back_to_index_btn)
    public void returnLastPage(View view) {
        this.finish();
    }

    @OnClick(R.id.forget_password_btn)
    public void skipToFindPasswordPage(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, GetVerificationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.eye)
    public void setRePasswordVisiable(View view) {
        ChangeVisiable.changeVisiable(Eye, password);
    }

    @OnClick(R.id.clear3)
    public void clearPassword(View view) {
        password.setText("");
    }

}
