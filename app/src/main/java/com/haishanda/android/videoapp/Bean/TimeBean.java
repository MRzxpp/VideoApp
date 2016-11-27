package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * Created by Zhongsz on 2016/11/25.
 */
@Entity
public class TimeBean {
    private int beginHour;
    private int beginMinute;
    private int endHour;
    private int endMinute;
    @Index
    @Id(autoincrement = false)
    private long machineId;
    @Generated(hash = 1198212568)
    public TimeBean(int beginHour, int beginMinute, int endHour, int endMinute,
            long machineId) {
        this.beginHour = beginHour;
        this.beginMinute = beginMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.machineId = machineId;
    }
    @Generated(hash = 1700076046)
    public TimeBean() {
    }
    public int getBeginHour() {
        return this.beginHour;
    }
    public void setBeginHour(int beginHour) {
        this.beginHour = beginHour;
    }
    public int getBeginMinute() {
        return this.beginMinute;
    }
    public void setBeginMinute(int beginMinute) {
        this.beginMinute = beginMinute;
    }
    public int getEndHour() {
        return this.endHour;
    }
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }
    public int getEndMinute() {
        return this.endMinute;
    }
    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
    public long getMachineId() {
        return this.machineId;
    }
    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }


}
