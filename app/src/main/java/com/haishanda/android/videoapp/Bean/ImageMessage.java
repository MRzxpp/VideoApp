package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/10/28.
 */
@Entity
public class ImageMessage {
    @Id(autoincrement = true)
    private Long id;
    private String parentDir;
    private String imgPath;
    private Date addTime;
    private Date updateTime;
    @Generated(hash = 1080324986)
    public ImageMessage(Long id, String parentDir, String imgPath, Date addTime,
            Date updateTime) {
        this.id = id;
        this.parentDir = parentDir;
        this.imgPath = imgPath;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
    @Generated(hash = 48598060)
    public ImageMessage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getParentDir() {
        return this.parentDir;
    }
    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }
    public String getImgPath() {
        return this.imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    public Date getAddTime() {
        return this.addTime;
    }
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
    public Date getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
