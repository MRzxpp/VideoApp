package com.haishanda.android.videoapp.Bean;

import java.util.Date;

/**
 * Created by Zhongsz on 2016/12/1.
 */

public class AlarmVo {
    private int id;
    private String machineName;
    private String urls;
    private Date alarmTime;

    public AlarmVo() {
    }

    public AlarmVo(int id, String machineName, String urls, Date alarmTime) {
        this.id = id;
        this.machineName = machineName;
        this.urls = urls;
        this.alarmTime = alarmTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
