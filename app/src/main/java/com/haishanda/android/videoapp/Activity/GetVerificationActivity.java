package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.EditChangedListener;
import com.haishanda.android.videoapp.Listener.FetchCodeListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.CountDownTimerUtil;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public class GetVerificationActivity extends Activity {
    @BindView(R.id.mobileNo_get_veri_text)
    EditText phoneNum;
    @BindView(R.id.fetch_code_getveri_input)
    EditText fetchCode;
    @BindView(R.id.reset_password_btn)
    Button toResetPwdPage;
    @BindView(R.id.get_code_btn)
    Button getCodeBtn;

    @BindColor(R.color.btnGrey)
    int textGrey;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getverification);
        ButterKnife.bind(this);
        toResetPwdPage.setEnabled(false);
        getCodeBtn.setEnabled(false);
        fetchCode.addTextChangedListener(new EditChangedListener(fetchCode, toResetPwdPage, blueBtn, greyBtn, white, white));
        phoneNum.addTextChangedListener(new FetchCodeListener(phoneNum, getCodeBtn, blueBtn, greyBtn, white, white));


    }

    @OnClick(R.id.reset_password_btn)
    public void skipToResetPasswordPage(View view) {
        if (!phoneNum.getText().toString().equals("")) {
            Intent intent = new Intent();
            intent.setClass(GetVerificationActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "请输入手机号和验证码", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.back_to_login_btn2)
    public void returnLastPage(View view) {
//        Intent intent = new Intent();
//        intent.setClass(GetVerificationActivity.this, LoginActivity.class);
//        startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.get_code_btn)
    public void send_fetch_code(View view) {
        CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(getCodeBtn, 120000, 1000, blueBtn, greyBtn, white, white);
        countDownTimerUtil.start();
        String mobileNo = phoneNum.getText().toString();
        ApiManage.getInstence().getUserApiService().getFetchCode(mobileNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i("info", "发送成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        Log.i("info", "正在发送");
                        Log.i("info", String.valueOf(smartResult.getCode()));
                    }

                });
    }

    @OnClick(R.id.meet_problem)
    public void skipToProblemPage(View view) {
        Intent intent = new Intent(GetVerificationActivity.this, ProblemActivity.class);
        startActivity(intent);
    }
}
