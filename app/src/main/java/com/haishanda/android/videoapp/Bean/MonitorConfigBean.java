package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/12/9.
 */
@Entity
public class MonitorConfigBean {
    @Unique
    @Id(autoincrement = false)
    private long machineId;
    private String boatName;
    private boolean isSwitchOn;
    @Generated(hash = 312048154)
    public MonitorConfigBean(long machineId, String boatName, boolean isSwitchOn) {
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
    public boolean getIsSwitchOn() {
        return this.isSwitchOn;
    }
    public void setIsSwitchOn(boolean isSwitchOn) {
        this.isSwitchOn = isSwitchOn;
    }

}
