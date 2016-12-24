package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/10/10.
 */
@Entity
public class VideoMessage {
    @Id(autoincrement = true)
    private Long id;
    private String parentDir;
    private String videoPath;
    private String addTime;
    private String iconPath;
    @Generated(hash = 394528003)
    public VideoMessage(Long id, String parentDir, String videoPath, String addTime,
            String iconPath) {
        this.id = id;
        this.parentDir = parentDir;
        this.videoPath = videoPath;
        this.addTime = addTime;
        this.iconPath = iconPath;
    }
    @Generated(hash = 407375025)
    public VideoMessage() {
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
    public String getVideoPath() {
        return this.videoPath;
    }
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    public String getAddTime() {
        return this.addTime;
    }
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
    public String getIconPath() {
        return this.iconPath;
    }
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

}
