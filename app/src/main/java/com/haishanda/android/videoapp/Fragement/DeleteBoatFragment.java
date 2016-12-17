package com.haishanda.android.videoapp.Fragement;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.Activity.MainActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.LoginMessage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;
import com.haishanda.android.videoapp.Utils.FileUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.LoginMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class DeleteBoatFragment extends Fragment {
    @BindView(R.id.boat_username_text)
    TextView boatUsername;
    @BindView(R.id.boat_password_text)
    EditText boatPassword;
    @BindView(R.id.eye4)
    TextView eye4;
    @BindView(R.id.clear4)
    ImageView clear4;
    @BindView(R.id.delete_boat_btn)
    Button nextStepBtn;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;
    @BindView(R.id.delete_video_and_photos_check)
    CheckBox checkBox;

    private final String Tag = "删除船舶";
    private String boatName;
    private Bundle data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_boat, container, false);
        ButterKnife.bind(this, view);
        LoginMessage loginMessage;
        LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
        QueryBuilder<LoginMessage> queryBuilder = loginMessageDao.queryBuilder();
        loginMessage = queryBuilder.uniqueOrThrow();
        boatUsername.setText(loginMessage.getUsername());
        data = getArguments();
        boatName = data.getString("boatName");
        clear4.setVisibility(View.INVISIBLE);
        boatPassword.addTextChangedListener(new ClearBtnListener(clear4, boatPassword));
        boatPassword.addTextChangedListener(new LoginListener(boatPassword, boatPassword, nextStepBtn, blueBtn, greyBtn, white, white));
        return view;
    }

    @OnClick(R.id.delete_boat_btn)
    public void skipToConfirmDeleteFragment() {
        int machineId = data.getInt("machineId");
        String password = boatPassword.getText().toString();
        deleteThisBoat(machineId, password);
    }

    @OnClick(R.id.back_to_boat_config_btn)
    public void backToBoatConfigActivity() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

    private void deleteThisBoat(int machineId, String password) {
        ApiManage.getInstence().getBoatApiService().removeBoat(machineId, password)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(Tag, "delete finished");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(Tag, "delete error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            Log.i(Tag, "delete successfully");
                            if (checkBox.isChecked()) {
                                deletePhotosAndVideos(boatName);
                            }
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.i(Tag, "delete failed!");
                        }
                    }
                });
    }

    private void deletePhotosAndVideos(String boatName) {
        File boatFile = new File("/sdcard/VideoApp/" + boatName);
        FileUtil.delete(boatFile);
    }


}
