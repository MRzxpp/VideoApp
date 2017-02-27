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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 获取套餐
 * Created by Zhongsz on 2016/11/14.
 */

public class AccountBalanceActivity extends Activity {
    @BindView(R.id.account_balance_main)
    ListView accountBalanceMain;

    private final String TAG = "获取套餐";
    List<PackageVo> packageVoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_balance);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToLastPafe() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    public void initViews() {
        Thread netThread = new Thread(new NetThread());
        netThread.start();
        try {
            netThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (packageVoList != null) {
            accountBalanceMain.setAdapter(new AccountBalanceAdapter(this, packageVoList));
        }
    }

    class NetThread implements Runnable {
        @Override
        public void run() {
            packageVoList = queryPackages();
        }
    }

    private List<PackageVo> queryPackages() {
        SharedPreferences preferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
        List<PackageVo> packageVoList = new ArrayList<>();
        Call<SmartResult<List<PackageVo>>> call = ApiManage.getInstence().getUserApiService().queryPackages(preferences.getString(Constant.USER_PREFERENCE_TOKEN, ""));
        try {
            Response<SmartResult<List<PackageVo>>> response = call.execute();
            if (response.body() != null) {
                if (response.body().getCode() == 1) {
                    Log.d(TAG, "success");
                    packageVoList = response.body().getData();
                    return packageVoList;
                } else {
                    Log.d(TAG, response.body().getMsg());
                    Toast.makeText(this, "获取套餐信息失败", Toast.LENGTH_LONG).show();
                    return null;
                }
            } else {
                Toast.makeText(this, "未获取到当前套餐信息", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packageVoList;
    }
}
