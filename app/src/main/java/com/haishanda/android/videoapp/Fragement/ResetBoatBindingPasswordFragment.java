package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.Log;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改船舶绑定密码
 * Created by Zhongsz on 2016/10/26.
 */

public class ResetBoatBindingPasswordFragment extends Fragment {
    private final String TAG = "重置船舶密码";
    private int machineId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_boat_pwd, container, false);
        ButterKnife.bind(this, view);
        Bundle data = this.getArguments();
        machineId = data.getInt("machineId");
        return view;
    }

    @OnClick(R.id.back_to_boat_config_btn)
    public void backToAboutBoatFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.get_new_boat_pwd_btn)
    public void getNewBoatPassword() {
        ApiManage.getInstence().getBoatApiService().resetBoatPassword(machineId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "reset completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "reset failed");
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            Toast.makeText(getActivity().getApplicationContext(), "已发送", Toast.LENGTH_SHORT).show();
                            backToAboutBoatFragment();
                        } else {
                            Log.i(TAG, smartResult.getMsg());
                            Toast.makeText(getActivity().getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
