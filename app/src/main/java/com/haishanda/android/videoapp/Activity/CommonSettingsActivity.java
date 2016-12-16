package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.haishanda.android.videoapp.Bean.FirstLogin;
import com.haishanda.android.videoapp.Fragement.ResetPasswordLoginedFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.Views.MaterialDialog;
import com.haishanda.android.videoapp.greendao.gen.AlarmNumDao;
import com.haishanda.android.videoapp.greendao.gen.AlarmVoBeanDao;
import com.haishanda.android.videoapp.greendao.gen.FirstLoginDao;
import com.haishanda.android.videoapp.greendao.gen.LastIdDao;
import com.haishanda.android.videoapp.greendao.gen.LoginMessageDao;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.hyphenate.chat.EMClient;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/12.
 */

public class CommonSettingsActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_settings);
        ButterKnife.bind(this);
    }

    public Activity getCommonSettingsActivity() {
        return this;
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToMyPage() {
        this.finish();
    }

    @OnClick(R.id.modify_password_layout)
    public void modifyPassword() {
        ResetPasswordLoginedFragment resetPasswordLoginedFragment = new ResetPasswordLoginedFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.common_settings_layout, resetPasswordLoginedFragment);
        fragmentTransaction.commit();

    }

    @OnClick(R.id.clear_buffer_layout)
    public void clearBuffer() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setTitle("确定清除缓存吗？");
        materialDialog.setMessage("清除缓存将会删除使用过程中产生的临时文件，以减小对手机储存空间的占用，不会对您的使用造成影响。");
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @OnClick(R.id.about_us_layout)
    public void skipToAboutUsPage() {

    }

    @OnClick(R.id.help_and_feedback_layout)
    public void skipToHelpAndFeedbackLayout() {
        Intent intent = new Intent(CommonSettingsActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.logout_btn)
    public void logOut() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage("确认退出吗?");
        dialog.setTitle("提示");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //监控数目重置
                AlarmVoBeanDao alarmVoBeanDao = VideoApplication.getApplication().getDaoSession().getAlarmVoBeanDao();
                alarmVoBeanDao.deleteAll();
                LastIdDao lastIdDao = VideoApplication.getApplication().getDaoSession().getLastIdDao();
                lastIdDao.deleteAll();
                //清除监控配置信息
                MonitorConfigBeanDao monitorConfigBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorConfigBeanDao();
                monitorConfigBeanDao.deleteAll();
                //报警数目归零
                AlarmNumDao alarmNumDao = VideoApplication.getApplication().getDaoSession().getAlarmNumDao();
                alarmNumDao.deleteAll();
                //重置是否第一次登录
                FirstLoginDao firstLoginDao = VideoApplication.getApplication().getDaoSession().getFirstLoginDao();
                FirstLogin firstLogin = new FirstLogin(1);
                firstLoginDao.deleteAll();
                firstLoginDao.insertOrReplace(firstLogin);
                //清除登录信息
                LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
                loginMessageDao.deleteAll();
                //退出环信
                EMClient.getInstance().logout(true);
                //返回欢迎页
                Intent intent = new Intent(getCommonSettingsActivity(), WelcomeActivity.class);
                startActivity(intent);
                getCommonSettingsActivity().finish();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
