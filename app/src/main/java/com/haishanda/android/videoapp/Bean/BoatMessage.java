package com.haishanda.android.videoapp.bean;

import org.greenrobot.greendao.annotation.Entity;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * 船舶的信息
 * Created by Zhongsz on 2016/11/16.
 */
@Entity
public class BoatMessage {
    @Index(unique = true)
    private int machineId;
    private String name;
    @Id(autoincrement = false)
    @Index(unique = true)
    private long cameraId;
    private String cameraImagePath;
    private String addTime;
    private String updateTime;

    @Generated(hash = 1893233633)
    public BoatMessage(int machineId, String name, long cameraId,
                       String cameraImagePath, String addTime, String updateTime) {
        this.machineId = machineId;
        this.name = name;
        this.cameraId = cameraId;
        this.cameraImagePath = cameraImagePath;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 1737508029)
    public BoatMessage() {
    }

    public int getMachineId() {
        return this.machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCameraId() {
        return this.cameraId;
    }

    public void setCameraId(long cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraImagePath() {
        return this.cameraImagePath;
    }

    public void setCameraImagePath(String cameraImagePath) {
        this.cameraImagePath = cameraImagePath;
    }

    public String getAddTime() {
        return this.addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }


}
