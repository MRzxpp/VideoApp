package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

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
    private String addTime;
    private String updateTime;
    @Generated(hash = 257556371)
    public ImageMessage(Long id, String parentDir, String imgPath, String addTime,
            String updateTime) {
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
