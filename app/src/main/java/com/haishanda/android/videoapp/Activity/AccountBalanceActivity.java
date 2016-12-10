package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Adapter.AccountBalanceAdapter;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.PackageVo;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    public void initViews() {
        packageVoList = queryPackages();
        if (packageVoList != null) {
            accountBalanceMain.setAdapter(new AccountBalanceAdapter(this, packageVoList));
        }
    }

    private List<PackageVo> queryPackages() {
        List<PackageVo> packageVoList = new ArrayList<>();
        Call<SmartResult<List<PackageVo>>> call = ApiManage.getInstence().getUserApiService().queryPackage(VideoApplication.getApplication().getToken());
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
