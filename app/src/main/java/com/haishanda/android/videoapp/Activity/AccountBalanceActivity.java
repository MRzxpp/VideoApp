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

    private final String TAG = "获取套餐";
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
                        Log.d(TAG, "获取套餐信息结束   ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "获取套餐信息失败   " + e.toString());
                        if (e instanceof SocketTimeoutException) {
                            Toast.makeText(instance, "连接服务器超时，请重试！", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (e instanceof ConnectException) {
                            Toast.makeText(instance, "连接服务器失败，请重试！", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (e instanceof IOException) {
                            Toast.makeText(instance, "连接服务器失败，请重试！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNext(SmartResult<List<PackageVo>> listSmartResult) {
                        if (listSmartResult != null) {
                            if (listSmartResult.getCode() == 1) {
                                Log.d(TAG, "success");
                                instance.packageVoList = listSmartResult.getData();
                                if (instance.packageVoList != null) {
                                    accountBalanceMain.setAdapter(new AccountBalanceAdapter(instance, instance.packageVoList));
                                }
                            } else {
                                Log.d(TAG, listSmartResult.getMsg());
                                Toast.makeText(instance, listSmartResult.getMsg() != null ? listSmartResult.getMsg() : "与服务器连接失败", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(instance, "未获取到当前套餐信息", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
