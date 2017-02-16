package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 每条船舶的监控信息
 * Created by Zhongsz on 2016/12/9.
 */
@Entity
public class MonitorConfigBean {
    @Unique
    @Id(autoincrement = false)
    private long machineId;
    private String boatName;
    private int isSwitchOn;

    @Generated(hash = 44471405)
    public MonitorConfigBean(long machineId, String boatName, int isSwitchOn) {
        this.machineId = machineId;
        this.boatName = boatName;
        this.isSwitchOn = isSwitchOn;
    }

    @Generated(hash = 435434227)
    public MonitorConfigBean() {
    }

    public long getMachineId() {
        return this.machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    public String getBoatName() {
        return this.boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public int getIsSwitchOn() {
        return this.isSwitchOn;
    }

    public void setIsSwitchOn(int isSwitchOn) {
        this.isSwitchOn = isSwitchOn;
    }


}
