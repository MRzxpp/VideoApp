package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 截图后保存的本地图片的信息
 * Created by Zhongsz on 2016/10/28.
 */
@Entity
public class ImageMessage {
    @Id(autoincrement = true)
    private Long id;
    private String boatName;
    private String imageName;
    private String addDate;
    private String updateTime;
    @Generated(hash = 1929080583)
    public ImageMessage(Long id, String boatName, String imageName, String addDate,
            String updateTime) {
        this.id = id;
        this.boatName = boatName;
        this.imageName = imageName;
        this.addDate = addDate;
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
    public String getBoatName() {
        return this.boatName;
    }
    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }
    public String getImageName() {
        return this.imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public String getAddDate() {
        return this.addDate;
    }
    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }
    public String getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
