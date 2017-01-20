package com.haishanda.android.videoapp.Activity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Bean.UserMessageBean;
import com.haishanda.android.videoapp.Config.Constant;
import com.haishanda.android.videoapp.Fragement.SetNicknameFragment;
import com.haishanda.android.videoapp.Fragement.SetPortraitFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.UserMessageBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人中心
 * Created by Zhongsz on 2016/11/26.
 */

public class MyCenterActivity extends FragmentActivity {
    @BindView(R.id.little_portrait)
    ImageView littlePortrait;

    @BindView(R.id.current_nickname)
    TextView currentNickname;

    @BindDrawable(R.drawable.default_portrait)
    Drawable defaultPortrait;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_center);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToLastPage() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.my_portrait_field)
    public void skipToEditPortrait() {
        SetPortraitFragment setPortraitFragment = new SetPortraitFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.my_center_layout, setPortraitFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.my_nickname_field)
    public void skipToEditNickname() {
        Bundle data = new Bundle();
        data.putLong("Id", userId);
        SetNicknameFragment setNicknameFragment = new SetNicknameFragment();
        setNicknameFragment.setArguments(data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
        fragmentTransaction.replace(R.id.my_center_layout, setNicknameFragment);
        fragmentTransaction.commit();
    }

    public void initViews() {
        SharedPreferences preferences = getSharedPreferences(Constant.USER_PREFERENCE, MODE_PRIVATE);
        long id = preferences.getInt(Constant.USER_PREFERENCE_ID, -1);
        this.userId = id;
        UserMessageBeanDao userMessageBeanDao = VideoApplication.getApplication().getDaoSession().getUserMessageBeanDao();
        QueryBuilder<UserMessageBean> queryBuilder = userMessageBeanDao.queryBuilder();
        UserMessageBean userMessageBean = queryBuilder.where(UserMessageBeanDao.Properties.Id.eq(id)).unique();
        Glide
                .with(this)
                .load(userMessageBean.getPortraitUrl())
                .error(defaultPortrait)
                .into(littlePortrait);
        currentNickname.setText(userMessageBean.getNickName() != null ? userMessageBean.getNickName() : "未设置");
    }

    public void refresh() {
        onResume();
    }
}
