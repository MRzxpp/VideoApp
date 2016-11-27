package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.LoginActivity;
import com.haishanda.android.videoapp.Activity.MyCenterActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.UserMessageBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.UserMessageBeanDao;


import org.greenrobot.greendao.query.QueryBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/11/26.
 */

public class SetNicknameFragment extends Fragment {
    @BindView(R.id.nickname_input)
    EditText nickNameInput;

    private final String TAG = "保存昵称";

    private long userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_nickname, container, false);
        ButterKnife.bind(this, view);
        Bundle data = getArguments();
        userId = data.getLong("Id");
        return view;
    }

    @OnClick(R.id.back_to_my_center_btn)
    public void backToLastPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.save_nickname_btn)
    public void saveNickName() {
        final String nickName = nickNameInput.getText().toString();
        if (nickName.equals("")) {
            Toast.makeText(getContext(), "请输入昵称！", Toast.LENGTH_LONG).show();
        } else {
            ApiManage.getInstence().getUserApiService().editNickName(VideoApplication.getApplication().getToken(), nickName)
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
                                Toast.makeText(getContext(), "修改成功！", Toast.LENGTH_LONG).show();
                                backToLastPage();
                                UserMessageBeanDao userMessageBeanDao = VideoApplication.getApplication().getDaoSession().getUserMessageBeanDao();
                                QueryBuilder<UserMessageBean> queryBuilder = userMessageBeanDao.queryBuilder();
                                UserMessageBean userMessageBean = queryBuilder.where(UserMessageBeanDao.Properties.Id.eq(userId)).unique();
                                userMessageBean.setNickName(nickName);
                                userMessageBeanDao.update(userMessageBean);
                                MyCenterActivity myCenterActivity = (MyCenterActivity) getActivity();
                                myCenterActivity.refresh();
                            } else {
                                Toast.makeText(getContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "修改未成功！", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
