package com.haishanda.android.videoapp.Activity;

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

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Utils.Watcher.EditChangedWatcher;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ViewUtil;

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
 * 忘记密码时重置密码
 * Created by Zhongsz on 2016/10/10.
 */

public class ResetPasswordUnloginedFragment extends Fragment {
    @BindView(R.id.reset_password_text)
    EditText resetPassword;
    @BindView(R.id.save_new_password_btn)
    Button savePasswordBtn;
    @BindView(R.id.eye3)
    TextView Eye3;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    private String mobileNo;
    private String TAG = "保存新密码";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password_unlogined, container, false);
        ButterKnife.bind(this, view);
        Bundle extra = getArguments();
        mobileNo = extra.getString("mobileNo");
        savePasswordBtn.setEnabled(false);
        resetPassword.addTextChangedListener(new EditChangedWatcher(resetPassword, savePasswordBtn, blueBtn, greyBtn, white, white));
        return view;
    }

    @OnClick(R.id.back_to_get_veri_btn)
    public void backToGetVeriPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_out, R.anim.slide_left_in);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.eye3)
    public void setRePasswordVisiable() {
        ViewUtil.changeVisiable(Eye3, resetPassword);
    }

    @OnClick(R.id.save_new_password_btn)
    public void saveNewPassword() {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$");
        Matcher passwordMatcher = passwordPattern.matcher(resetPassword.getText().toString());
        if (resetPassword.getText().toString().length() < 6) {
            Toast.makeText(getActivity().getApplicationContext(), "密码长度不足6位，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (resetPassword.getText().toString().length() > 20) {
            Toast.makeText(getActivity().getApplicationContext(), "密码长度过长，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else if (!passwordMatcher.matches()) {
            Toast.makeText(getActivity().getApplicationContext(), "密码过于简单，请重新输入", Toast.LENGTH_SHORT)
                    .show();
        } else {
            ApiManage.getInstence().getUserApiService().forgetPassword(mobileNo, resetPassword.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i(TAG, "completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "error");
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(), "网络连接错误", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                Log.i(TAG, "reset success");
                                Toast.makeText(getActivity().getApplicationContext(), "修改成功，请重新登录！", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                                getActivity().finish();
                            } else {
                                Log.i(TAG, "reset failed");
                                Toast.makeText(getActivity().getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "修改失败", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }
    }
}
