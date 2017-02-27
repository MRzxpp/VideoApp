package com.haishanda.android.videoapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.activity.BoatConfigActivity;
import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.DaoUtil;
import com.haishanda.android.videoapp.VideoApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class RenameBoatFragment extends Fragment {
    @BindView(R.id.boat_new_name)
    EditText boatNewName;
    @BindView(R.id.clear5)
    ImageView clear5;
    @BindView(R.id.save_rename_boat_btn)
    TextView saveBoatNameBtn;


    private int machineId;
    private String originalBoatName;
    private final String TAG = "修改船名";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rename_boat, container, false);
        ButterKnife.bind(this, view);
        Bundle data = getArguments();
        originalBoatName = data.getString("boatName");
        boatNewName.setText(originalBoatName != null ? originalBoatName : "请输入船舶名称");
        machineId = data.getInt("machineId");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        originalBoatName = VideoApplication.getApplication().getCurrentBoatName();
        boatNewName.setText(originalBoatName != null ? originalBoatName : "请输入船舶名称");
    }

    @OnClick(R.id.save_rename_boat_btn)
    public void saveBoatNewName() {
        //Todo this regex is not correct
        Pattern pattern = Pattern.compile("^((?=.*[0-9a-zA-Z]).{0,16})|((?=.*[\\u4e00-\\u9fa5]).{0,8})$");
        Matcher matcher = pattern.matcher(boatNewName.getText().toString());
        if (matcher.matches()) {
            saveBoatNameBtn.setEnabled(false);
            ApiManage.getInstence().getBoatApiService().editMachineName(machineId, boatNewName.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "completed");
                            saveBoatNameBtn.setEnabled(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "error");
                            e.printStackTrace();
                            Toast.makeText(getContext(), "网络连接错误", Toast.LENGTH_LONG).show();
                            saveBoatNameBtn.setEnabled(true);
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                saveBoatNameBtn.setEnabled(true);
                                Toast.makeText(getContext(), "修改成功", Toast.LENGTH_LONG).show();
                                //对数据库进行操作
                                DaoUtil.renameBoat(boatNewName.getText().toString(), originalBoatName, machineId);
                                BoatConfigActivity boatConfigActivity = (BoatConfigActivity) getActivity();
                                boatConfigActivity.instance.refresh();
                                backToFrontPage();
                            } else {
                                Log.d(TAG, "failed");
                                Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "修改失败", Toast.LENGTH_SHORT).show();
                                saveBoatNameBtn.setEnabled(true);
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "船舶名称限制为8个汉字或16个数字、字母", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.back_to_boat_config_btn)
    public void backToFrontPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
