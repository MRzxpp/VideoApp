package com.haishanda.android.videoapp.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.utils.textwatcher.LoginWatcher;
import com.haishanda.android.videoapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * 该页面为重置密码的页面
 * Created by Zhongsz on 2016/11/24.
 */

public class ResetPasswordFragment extends Fragment {
    @BindView(R.id.reset_password_text)
    EditText resetPasswordText;
    @BindView(R.id.save_new_password_btn_logined)
    Button saveNewPasswordBtnLogined;
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
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        ButterKnife.bind(this, view);
        resetPasswordText.addTextChangedListener(new LoginWatcher(resetPasswordText, resetPasswordText, saveNewPasswordBtnLogined, blueBtn, greyBtn, white, white));
        return view;
    }

    @OnClick(R.id.save_new_password_btn_logined)
    public void saveNewPassword() {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$");
        Matcher passwordMatcher = passwordPattern.matcher(resetPasswordText.getText().toString());
        if (resetPasswordText.getText().toString().length() < 6) {
            Toast.makeText(getContext(), "密码长度不足6位，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (resetPasswordText.getText().toString().length() > 20) {
            Toast.makeText(getContext(), "密码长度过长，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (!passwordMatcher.matches()) {
            Toast.makeText(getContext(), "密码过于简单，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else {
            ApiManage.getInstence().getUserApiServiceWithToken().resetPassword(resetPasswordText.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i(TAG, "reset completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "reset error");
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                Toast.makeText(getContext(), "修改成功！", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            } else {
                                Log.d(TAG, String.valueOf(smartResult.getCode()));
                                Log.d(TAG, smartResult.getMsg());
                                Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "修改未成功！", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
