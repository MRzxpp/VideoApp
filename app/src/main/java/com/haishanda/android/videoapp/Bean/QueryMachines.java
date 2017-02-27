package com.haishanda.android.videoapp.bean;

/**
 * 储存船舶信息
 * Created by Zhongsz on 2016/11/15.
 */

public class QueryMachines {

    /**
     * id : 2
     * name : 海善达2号
     * globalId : 38491779-d637-49c6-8c78-c3dfdbd6f797
     * netModuleId : 1
     * begin : 0
     * span : 0
     * addTime : 1479107785000
     * updateTime : 1479109680000
     * switchOn : false
     */

    private int id;
    private String name;
    private String globalId;
    private String netModuleId;
    private String begin;
    private String span;
    private String addTime;
    private String updateTime;
    private boolean switchOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getNetModuleId() {
        return netModuleId;
    }

    public void setNetModuleId(String netModuleId) {
        this.netModuleId = netModuleId;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public void setSwitchOn(boolean switchOn) {
        this.switchOn = switchOn;
    }
}
