package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Activity.AccountBalanceActivity;
import com.haishanda.android.videoapp.Activity.CommonSettingsActivity;
import com.haishanda.android.videoapp.Activity.MessageCenterActivity;
import com.haishanda.android.videoapp.Activity.MyCenterActivity;
import com.haishanda.android.videoapp.Bean.LoginMessage;
import com.haishanda.android.videoapp.Bean.UserMessageBean;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.LoginMessageDao;
import com.haishanda.android.videoapp.greendao.gen.UserMessageBeanDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/14.
 */

public class MyFragment extends Fragment {
    @BindView(R.id.my_phone_number)
    TextView myPhoneNumber;

    @BindView(R.id.my_head_img)
    ImageView portrait;
    @BindView(R.id.my_nickname)
    TextView nickName;
    @BindDrawable(R.drawable.default_portrait)
    Drawable defaultPortrait;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
        QueryBuilder<LoginMessage> queryBuilder = loginMessageDao.queryBuilder();
        try {
            LoginMessage loginMessage = queryBuilder.uniqueOrThrow();
            myPhoneNumber.setText(confusePhoneNum(loginMessage.getUsername()));
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return view;
    }

    @OnClick(R.id.message_center)
    public void skipToMessageCenter(View view) {
        Intent intent = new Intent(getActivity(), MessageCenterActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @OnClick(R.id.common_settings)
    public void skipToCommonSettings(View view) {
        Intent intent = new Intent(getActivity(), CommonSettingsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @OnClick(R.id.account_balance)
    public void skipToAccountBalance(View view) {
        Intent intent = new Intent(getActivity(), AccountBalanceActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    private String confusePhoneNum(String phoneNum) {
        return phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, 11);
    }

    @OnClick(R.id.to_my_center)
    public void skipToMyCenter() {
        Intent intent = new Intent(getActivity(), MyCenterActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    private void initViews() {
        LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
        QueryBuilder<LoginMessage> loginMessageQueryBuilder = loginMessageDao.queryBuilder();
         long id = loginMessageQueryBuilder.unique().getId();
        UserMessageBeanDao userMessageBeanDao = VideoApplication.getApplication().getDaoSession().getUserMessageBeanDao();
        QueryBuilder<UserMessageBean> queryBuilder = userMessageBeanDao.queryBuilder();
        UserMessageBean userMessageBean = queryBuilder.where(UserMessageBeanDao.Properties.Id.eq(id)).unique();
        Glide
                .with(this)
                .load(userMessageBean.getPortraitUrl())
                .error(defaultPortrait)
                .into(portrait);
        nickName.setText(userMessageBean.getNickName() != null ? userMessageBean.getNickName() : "请输入昵称");
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

}
