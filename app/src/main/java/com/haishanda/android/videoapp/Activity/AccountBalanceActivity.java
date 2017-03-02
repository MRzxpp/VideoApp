package com.haishanda.android.videoapp.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.haishanda.android.videoapp.adapter.AccountBalanceAdapter;
import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.bean.PackageVo;
import com.haishanda.android.videoapp.config.Constant;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.config.StringConstant;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取套餐
 * Created by Zhongsz on 2016/11/14.
 */

public class AccountBalanceActivity extends Activity {
    @BindView(R.id.account_balance_main)
    ListView accountBalanceMain;

    private static final String TAG = "获取套餐";
    private static final String GET_PACKAGE_SUCCESS = "获取套餐成功";
    private static final String GET_PACKAGE_FAIL = "获取套餐失败";
    private static final String GET_PACKAGE_COMPLETED = "获取套餐完成";
    List<PackageVo> packageVoList;
    private AccountBalanceActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_balance);
        ButterKnife.bind(this);
        if (instance == null) {
            synchronized (AccountBalanceActivity.class) {
                if (instance == null) {
                    instance = this;
                }
            }
        }
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToLastPafe() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryPackages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private void queryPackages() {
        SharedPreferences preferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
        ApiManage.getInstence().getUserApiService().queryPackages(preferences.getString(Constant.USER_PREFERENCE_TOKEN, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult<List<PackageVo>>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, GET_PACKAGE_COMPLETED);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, GET_PACKAGE_FAIL + e.toString());
                        if (e instanceof SocketTimeoutException) {
                            Toast.makeText(instance, StringConstant.MESSAGE_CONNECT_SERVER_TIMEOUT, Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (e instanceof ConnectException) {
                            Toast.makeText(instance, StringConstant.MESSAGE_CONNECT_SERVER_FAIL, Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (e instanceof IOException) {
                            Toast.makeText(instance, StringConstant.MESSAGE_CONNECT_SERVER_FAIL, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNext(SmartResult<List<PackageVo>> listSmartResult) {
                        if (listSmartResult != null) {
                            if (listSmartResult.getCode() == 1) {
                                Log.d(TAG, GET_PACKAGE_SUCCESS);
                                instance.packageVoList = listSmartResult.getData();
                                if (instance.packageVoList != null) {
                                    accountBalanceMain.setAdapter(new AccountBalanceAdapter(instance, instance.packageVoList));
                                }
                            } else {
                                Log.d(TAG, listSmartResult.getMsg());
                                Toast.makeText(instance, listSmartResult.getMsg() != null ? listSmartResult.getMsg() : StringConstant.MESSAGE_SERVER_RETURN_FALSE, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(instance, StringConstant.MESSAGE_SERVER_RETURN_FALSE, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
