package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 每一条监控消息所包含的参数
 * Created by Zhongsz on 2016/12/12.
 */
@Entity
public class AlarmVoBean {
    @Id(autoincrement = false)
    @Unique
    private long id;
    private String machineName;
    private String urls;
    private Date alarmTime;

    @Generated(hash = 1501717757)
    public AlarmVoBean(long id, String machineName, String urls, Date alarmTime) {
        this.id = id;
        this.machineName = machineName;
        this.urls = urls;
        this.alarmTime = alarmTime;
    }

    @Generated(hash = 1559237878)
    public AlarmVoBean() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMachineName() {
        return this.machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getUrls() {
        return this.urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public Date getAlarmTime() {
        return this.alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }
}
