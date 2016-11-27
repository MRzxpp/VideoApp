package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.ProblemActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.LoginMessage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.Listener.SignUpListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.CountDownTimerUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.LoginMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/11/24.
 */

public class ResetPasswordLoginedFragment extends Fragment {
    @BindView(R.id.logined_phonenum)
    TextView loginedPhoneNumber;
    @BindView(R.id.logined_code_input)
    EditText loginedCodeInput;
    @BindView(R.id.get_code_btn_logined)
    Button getCodeLogined;
    @BindView(R.id.to_resetpwd_with_token2)
    Button toResetPwdWithToken2Btn;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    private String phoneNumLogined;
    private final String TAG = "修改密码";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password_with_token, container, false);
        ButterKnife.bind(this, view);
        LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
        QueryBuilder<LoginMessage> queryBuilder = loginMessageDao.queryBuilder();
        phoneNumLogined = queryBuilder.uniqueOrThrow().getUsername();
        loginedPhoneNumber.setText("验证当前账号绑定的密保手机：" + confusePhoneNum(phoneNumLogined));
        loginedCodeInput.addTextChangedListener(new LoginListener(loginedCodeInput, loginedCodeInput, toResetPwdWithToken2Btn, blueBtn, greyBtn, white, white));
        return view;
    }

    @OnClick(R.id.back_to_common_settings)
    public void backToLastPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.get_code_btn_logined)
    public void getCodeLogined() {
        CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(getCodeLogined, 120000, 1000, blueBtn, greyBtn, white, white);
        countDownTimerUtil.start();
        ApiManage.getInstence().getUserApiServiceWithToken().getFetchCodeWithToken(VideoApplication.getApplication().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "modify completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "modify error");
                        Toast.makeText(getContext(), "连接失败！", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            Toast.makeText(getContext(), "发送成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "发送失败，请稍后重试", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @OnClick(R.id.to_resetpwd_with_token2)
    public void toResetPasswordLoginedFragment2() {
        ApiManage.getInstence().getUserApiServiceWithToken().validateCode(loginedCodeInput.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "validate completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "validate error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            ResetPasswordLoginedFragment2 resetPasswordLoginedFragment2 = new ResetPasswordLoginedFragment2();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.reset_password_with_token_layout, resetPasswordLoginedFragment2);
                            fragmentTransaction.commit();
                        } else {
                            Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "验证码输入错误", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @OnClick(R.id.meet_problem)
    public void toProblemPage() {
        Intent intent = new Intent(getActivity(), ProblemActivity.class);
        startActivity(intent);
    }

    private String confusePhoneNum(String phoneNum) {
        return phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, 11);
    }
}
