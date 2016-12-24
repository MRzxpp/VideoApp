package com.haishanda.android.videoapp.Fragement;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.BoatMessage;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.Bean.VideoMessage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.List;
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
        boatNewName.setHint(originalBoatName != null ? originalBoatName : "请输入船舶名称");
        machineId = data.getInt("machineId");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        originalBoatName = VideoApplication.getApplication().getCurrentBoatName();
        boatNewName.setHint(originalBoatName != null ? originalBoatName : "请输入船舶名称");
    }

    @OnClick(R.id.save_rename_boat_btn)
    public void saveBoatNewName() {
        //Todo this regex is not correct
        Pattern pattern = Pattern.compile("^((?=.*[0-9a-zA-Z]).{0,16})|((?=.*[\\u4e00-\\u9fa5]).{0,8})$");
        Matcher matcher = pattern.matcher(boatNewName.getText().toString());
        if (matcher.matches()) {
            ApiManage.getInstence().getBoatApiService().editMachineName(machineId, boatNewName.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "error");
                            e.printStackTrace();
                            Toast.makeText(getContext(), "网络连接错误", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                Toast.makeText(getContext(), "修改成功", Toast.LENGTH_LONG).show();
                                ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
                                BoatMessageDao boatMessageDao = VideoApplication.getApplication().getDaoSession().getBoatMessageDao();
                                VideoMessageDao videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
                                QueryBuilder<BoatMessage> boatQuery = boatMessageDao.queryBuilder();
                                QueryBuilder<ImageMessage> queryBuilder = imageMessageDao.queryBuilder();
                                QueryBuilder<VideoMessage> videoMessageQueryBuilder = videoMessageDao.queryBuilder();
                                List<BoatMessage> boatMessages = boatQuery.where(BoatMessageDao.Properties.MachineId.eq(machineId)).list();
                                List<ImageMessage> imageMessageList = queryBuilder.where(ImageMessageDao.Properties.ParentDir.eq(originalBoatName)).list();
                                List<VideoMessage> videoMessageList = videoMessageQueryBuilder.where(VideoMessageDao.Properties.ParentDir.eq(originalBoatName)).list();
                                for (BoatMessage boatMessage : boatMessages
                                        ) {
                                    String oldIconPath = boatMessage.getCameraImagePath();
                                    String newIconPath = oldIconPath.replace(originalBoatName, boatNewName.getText().toString());
                                    boatMessage.setCameraImagePath(newIconPath);
                                    boatMessageDao.update(boatMessage);
                                }
                                for (ImageMessage imageMessage : imageMessageList
                                        ) {
                                    imageMessage.setParentDir(boatNewName.getText().toString());
                                    imageMessageDao.update(imageMessage);
                                }
                                for (VideoMessage videoMessage : videoMessageList
                                        ) {
                                    videoMessage.setParentDir(boatNewName.getText().toString());
                                    videoMessageDao.update(videoMessage);
                                }
                                File originalDir = new File(Environment.getExternalStorageDirectory().getPath() + "/VideoApp/" + originalBoatName);
                                File newDir = new File(Environment.getExternalStorageDirectory().getPath() + "/VideoApp/" + boatNewName.getText().toString());
                                originalDir.renameTo(newDir);
                                VideoApplication.getApplication().setCurrentBoatName(boatNewName.getText().toString());
                                BoatConfigActivity boatConfigActivity = (BoatConfigActivity) getActivity();
                                boatConfigActivity.instance.refresh();
                                backToFrontPage();
                            } else {
                                Log.d(TAG, "failed");
                                Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "修改失败", Toast.LENGTH_SHORT).show();
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
