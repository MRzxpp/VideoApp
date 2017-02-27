package com.haishanda.android.videoapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.haishanda.android.videoapp.activity.ProblemActivity;
import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.config.Constant;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.utils.textwatcher.LoginWatcher;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.CountDownTimerUtil;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 在已登录的情况下重置密码
 * 该页面为获取及验证验证码的页面
 * Created by Zhongsz on 2016/11/24.
 */

public class VerifyCodeFragment extends Fragment {
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

    private final String TAG = "修改密码";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_code, container, false);
        ButterKnife.bind(this, view);
        SharedPreferences preferences = getActivity().getSharedPreferences(Constant.USER_PREFERENCE, Context.MODE_PRIVATE);
        String phoneNumLogined = preferences.getString(Constant.USER_PREFERENCE_USERNAME, "");
        loginedPhoneNumber.setText("验证当前账号绑定的密保手机：" + confusePhoneNum(phoneNumLogined));
        loginedCodeInput.addTextChangedListener(new LoginWatcher(loginedCodeInput, loginedCodeInput, toResetPwdWithToken2Btn, blueBtn, greyBtn, white, white));
        return view;
    }

    @OnClick(R.id.back_to_common_settings)
    public void backToLastPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.get_code_btn_logined)
    public void getCodeLogined() {
        CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(getCodeLogined, 120000, 1000, blueBtn, greyBtn, white, white);
        countDownTimerUtil.start();
        SharedPreferences preferences = getActivity().getSharedPreferences(Constant.USER_PREFERENCE, Context.MODE_PRIVATE);
        ApiManage.getInstence().getUserApiServiceWithToken().getFetchCodeWithToken(preferences.getString(Constant.USER_PREFERENCE_TOKEN, ""))
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
                            Toast.makeText(getContext(), "已发送", Toast.LENGTH_LONG).show();
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
                            ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
                            fragmentTransaction.replace(R.id.reset_password_with_token_layout, resetPasswordFragment);
                            fragmentTransaction.commit();
                        } else {
                            Log.d(TAG, String.valueOf(smartResult.getCode()));
                            Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "验证码输入错误", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @OnClick(R.id.meet_problem)
    public void toProblemPage() {
        Intent intent = new Intent(getActivity(), ProblemActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    private String confusePhoneNum(String phoneNum) {
        return phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, 11);
    }
}
