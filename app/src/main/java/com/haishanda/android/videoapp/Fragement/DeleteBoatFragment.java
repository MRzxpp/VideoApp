package com.haishanda.android.videoapp.Fragement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class DeleteBoatFragment extends Fragment {
    @BindView(R.id.boat_username_text)
    EditText boatUsername;
    @BindView(R.id.boat_password_text)
    EditText boatPassword;
    @BindView(R.id.eye4)
    TextView eye4;
    @BindView(R.id.clear4)
    ImageView clear4;
    @BindView(R.id.delete_boat_next_step_btn)
    Button nextStepBtn;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    private final String Tag = "删除船舶";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_boat, container, false);
        ButterKnife.bind(this, view);
        clear4.setVisibility(View.INVISIBLE);
        boatPassword.addTextChangedListener(new ClearBtnListener(clear4, boatPassword));
        boatPassword.addTextChangedListener(new LoginListener(boatUsername, boatPassword, nextStepBtn, blueBtn, greyBtn, white, white));
        return view;
    }

    @OnClick(R.id.delete_boat_next_step_btn)
    public void skipToConfirmDeleteFragment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("确定删除本船么?");
        builder.setTitle("警告");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteThisBoat();
                ConfirmDeleteBoatFragment confirmDeleteBoatFragment = new ConfirmDeleteBoatFragment();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.delete_boat_message, confirmDeleteBoatFragment);
                fragmentTransaction.commit();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    @OnClick(R.id.back_to_boat_config_btn)
    public void backToBoatConfigActivity() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.eye4)
    public void setBoatPasswordVisiable(View view) {
        ChangeVisiable.changeVisiable(eye4, boatPassword);
    }

    @OnClick(R.id.clear4)
    public void clearBoatPassword(View view) {
        boatPassword.setText("");
    }

    private void deleteThisBoat() {
        String machineId = "";
        String password = "";
        ApiManage.getInstence().getBoatApiService().removeBoat(machineId, password)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(Tag, "add finished");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(Tag, "add error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            Log.i(Tag, "add successfully");
                            Toast.makeText(getContext(), "添加船舶成功", Toast.LENGTH_LONG).show();
                        } else {
                            Log.i(Tag, "add failed!");
                            Toast.makeText(getContext(), smartResult.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
