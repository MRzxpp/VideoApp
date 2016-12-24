package com.haishanda.android.videoapp.Bean;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public class UserBean {
    private int id;
    private String globalId;
    private String name;
    private String token;
    private String portrait;
    private int isAlarmSms;

    public int getIsAlarmSms() {
        return isAlarmSms;
    }

    public void setIsAlarmSms(int isAlarmSms) {
        this.isAlarmSms = isAlarmSms;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
