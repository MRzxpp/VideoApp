package com.haishanda.android.videoapp.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.haishanda.android.videoapp.Bean.AlarmNum;
import com.haishanda.android.videoapp.Bean.AlarmVoBean;
import com.haishanda.android.videoapp.Bean.BoatMessage;
import com.haishanda.android.videoapp.Bean.FirstLogin;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.Bean.LastId;
import com.haishanda.android.videoapp.Bean.LoginMessage;
import com.haishanda.android.videoapp.Bean.MonitorConfigBean;
import com.haishanda.android.videoapp.Bean.TimeBean;
import com.haishanda.android.videoapp.Bean.UserMessageBean;
import com.haishanda.android.videoapp.Bean.VideoMessage;

import com.haishanda.android.videoapp.greendao.gen.AlarmNumDao;
import com.haishanda.android.videoapp.greendao.gen.AlarmVoBeanDao;
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;
import com.haishanda.android.videoapp.greendao.gen.FirstLoginDao;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;
import com.haishanda.android.videoapp.greendao.gen.LastIdDao;
import com.haishanda.android.videoapp.greendao.gen.LoginMessageDao;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.haishanda.android.videoapp.greendao.gen.TimeBeanDao;
import com.haishanda.android.videoapp.greendao.gen.UserMessageBeanDao;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig alarmNumDaoConfig;
    private final DaoConfig alarmVoBeanDaoConfig;
    private final DaoConfig boatMessageDaoConfig;
    private final DaoConfig firstLoginDaoConfig;
    private final DaoConfig imageMessageDaoConfig;
    private final DaoConfig lastIdDaoConfig;
    private final DaoConfig loginMessageDaoConfig;
    private final DaoConfig monitorConfigBeanDaoConfig;
    private final DaoConfig timeBeanDaoConfig;
    private final DaoConfig userMessageBeanDaoConfig;
    private final DaoConfig videoMessageDaoConfig;

    private final AlarmNumDao alarmNumDao;
    private final AlarmVoBeanDao alarmVoBeanDao;
    private final BoatMessageDao boatMessageDao;
    private final FirstLoginDao firstLoginDao;
    private final ImageMessageDao imageMessageDao;
    private final LastIdDao lastIdDao;
    private final LoginMessageDao loginMessageDao;
    private final MonitorConfigBeanDao monitorConfigBeanDao;
    private final TimeBeanDao timeBeanDao;
    private final UserMessageBeanDao userMessageBeanDao;
    private final VideoMessageDao videoMessageDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        alarmNumDaoConfig = daoConfigMap.get(AlarmNumDao.class).clone();
        alarmNumDaoConfig.initIdentityScope(type);

        alarmVoBeanDaoConfig = daoConfigMap.get(AlarmVoBeanDao.class).clone();
        alarmVoBeanDaoConfig.initIdentityScope(type);

        boatMessageDaoConfig = daoConfigMap.get(BoatMessageDao.class).clone();
        boatMessageDaoConfig.initIdentityScope(type);

        firstLoginDaoConfig = daoConfigMap.get(FirstLoginDao.class).clone();
        firstLoginDaoConfig.initIdentityScope(type);

        imageMessageDaoConfig = daoConfigMap.get(ImageMessageDao.class).clone();
        imageMessageDaoConfig.initIdentityScope(type);

        lastIdDaoConfig = daoConfigMap.get(LastIdDao.class).clone();
        lastIdDaoConfig.initIdentityScope(type);

        loginMessageDaoConfig = daoConfigMap.get(LoginMessageDao.class).clone();
        loginMessageDaoConfig.initIdentityScope(type);

        monitorConfigBeanDaoConfig = daoConfigMap.get(MonitorConfigBeanDao.class).clone();
        monitorConfigBeanDaoConfig.initIdentityScope(type);

        timeBeanDaoConfig = daoConfigMap.get(TimeBeanDao.class).clone();
        timeBeanDaoConfig.initIdentityScope(type);

        userMessageBeanDaoConfig = daoConfigMap.get(UserMessageBeanDao.class).clone();
        userMessageBeanDaoConfig.initIdentityScope(type);

        videoMessageDaoConfig = daoConfigMap.get(VideoMessageDao.class).clone();
        videoMessageDaoConfig.initIdentityScope(type);

        alarmNumDao = new AlarmNumDao(alarmNumDaoConfig, this);
        alarmVoBeanDao = new AlarmVoBeanDao(alarmVoBeanDaoConfig, this);
        boatMessageDao = new BoatMessageDao(boatMessageDaoConfig, this);
        firstLoginDao = new FirstLoginDao(firstLoginDaoConfig, this);
        imageMessageDao = new ImageMessageDao(imageMessageDaoConfig, this);
        lastIdDao = new LastIdDao(lastIdDaoConfig, this);
        loginMessageDao = new LoginMessageDao(loginMessageDaoConfig, this);
        monitorConfigBeanDao = new MonitorConfigBeanDao(monitorConfigBeanDaoConfig, this);
        timeBeanDao = new TimeBeanDao(timeBeanDaoConfig, this);
        userMessageBeanDao = new UserMessageBeanDao(userMessageBeanDaoConfig, this);
        videoMessageDao = new VideoMessageDao(videoMessageDaoConfig, this);

        registerDao(AlarmNum.class, alarmNumDao);
        registerDao(AlarmVoBean.class, alarmVoBeanDao);
        registerDao(BoatMessage.class, boatMessageDao);
        registerDao(FirstLogin.class, firstLoginDao);
        registerDao(ImageMessage.class, imageMessageDao);
        registerDao(LastId.class, lastIdDao);
        registerDao(LoginMessage.class, loginMessageDao);
        registerDao(MonitorConfigBean.class, monitorConfigBeanDao);
        registerDao(TimeBean.class, timeBeanDao);
        registerDao(UserMessageBean.class, userMessageBeanDao);
        registerDao(VideoMessage.class, videoMessageDao);
    }
    
    public void clear() {
        alarmNumDaoConfig.clearIdentityScope();
        alarmVoBeanDaoConfig.clearIdentityScope();
        boatMessageDaoConfig.clearIdentityScope();
        firstLoginDaoConfig.clearIdentityScope();
        imageMessageDaoConfig.clearIdentityScope();
        lastIdDaoConfig.clearIdentityScope();
        loginMessageDaoConfig.clearIdentityScope();
        monitorConfigBeanDaoConfig.clearIdentityScope();
        timeBeanDaoConfig.clearIdentityScope();
        userMessageBeanDaoConfig.clearIdentityScope();
        videoMessageDaoConfig.clearIdentityScope();
    }

    public AlarmNumDao getAlarmNumDao() {
        return alarmNumDao;
    }

    public AlarmVoBeanDao getAlarmVoBeanDao() {
        return alarmVoBeanDao;
    }

    public BoatMessageDao getBoatMessageDao() {
        return boatMessageDao;
    }

    public FirstLoginDao getFirstLoginDao() {
        return firstLoginDao;
    }

    public ImageMessageDao getImageMessageDao() {
        return imageMessageDao;
    }

    public LastIdDao getLastIdDao() {
        return lastIdDao;
    }

    public LoginMessageDao getLoginMessageDao() {
        return loginMessageDao;
    }

    public MonitorConfigBeanDao getMonitorConfigBeanDao() {
        return monitorConfigBeanDao;
    }

    public TimeBeanDao getTimeBeanDao() {
        return timeBeanDao;
    }

    public UserMessageBeanDao getUserMessageBeanDao() {
        return userMessageBeanDao;
    }

    public VideoMessageDao getVideoMessageDao() {
        return videoMessageDao;
    }

}
