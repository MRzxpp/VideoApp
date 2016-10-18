package com.haishanda.android.videoapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.haishanda.android.videoapp.Fragement.BoatFragment;
import com.haishanda.android.videoapp.Fragement.MonitorFragment;
import com.haishanda.android.videoapp.Fragement.MyFragment;
import com.haishanda.android.videoapp.Fragement.PhotosFragment;
import com.haishanda.android.videoapp.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setDefaultFragment();
        //初始化环信
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setRequireAck(true);
        options.setRequireDeliveryAck(true);
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }

    public void skipToLoginPage(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        BoatFragment boatFragment = new BoatFragment();
        transaction.replace(R.id.fragment_main, boatFragment);
        transaction.commit();
    }

    @OnClick({R.id.to_boat_fragment, R.id.to_photos_fragment, R.id.to_monitor_fragment, R.id.to_my_fragment})
    public void skipToBoatFragment(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case (R.id.to_boat_fragment): {
                BoatFragment boatFragment = new BoatFragment();
                fragmentTransaction.replace(R.id.fragment_main, boatFragment);
                break;
            }
            case (R.id.to_photos_fragment): {
                PhotosFragment photosFragment = new PhotosFragment();
                fragmentTransaction.replace(R.id.fragment_main, photosFragment);
                break;
            }
            case (R.id.to_monitor_fragment): {
                MonitorFragment monitorFragment = new MonitorFragment();
                fragmentTransaction.replace(R.id.fragment_main, monitorFragment);
                break;
            }
            case (R.id.to_my_fragment): {
                MyFragment myFragment = new MyFragment();
                fragmentTransaction.replace(R.id.fragment_main, myFragment);
                break;
            }
            default:
                break;
        }
        fragmentTransaction.commit();

    }

}
