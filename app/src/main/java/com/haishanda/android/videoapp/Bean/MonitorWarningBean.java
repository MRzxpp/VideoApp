package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/12/7.
 */
@Entity
public class MonitorWarningBean {
    @Id(autoincrement = false)
    long id;
    private boolean isNotificationOpen;
    private boolean isVoiceOpen;
    private boolean isShakeOpen;
    private boolean isSendingEmail;
    @Generated(hash = 1631536719)
    public MonitorWarningBean(long id, boolean isNotificationOpen,
            boolean isVoiceOpen, boolean isShakeOpen, boolean isSendingEmail) {
        this.id = id;
        this.isNotificationOpen = isNotificationOpen;
        this.isVoiceOpen = isVoiceOpen;
        this.isShakeOpen = isShakeOpen;
        this.isSendingEmail = isSendingEmail;
    }
    @Generated(hash = 725060134)
    public MonitorWarningBean() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean getIsNotificationOpen() {
        return this.isNotificationOpen;
    }
    public void setIsNotificationOpen(boolean isNotificationOpen) {
        this.isNotificationOpen = isNotificationOpen;
    }
    public boolean getIsVoiceOpen() {
        return this.isVoiceOpen;
    }
    public void setIsVoiceOpen(boolean isVoiceOpen) {
        this.isVoiceOpen = isVoiceOpen;
    }
    public boolean getIsShakeOpen() {
        return this.isShakeOpen;
    }
    public void setIsShakeOpen(boolean isShakeOpen) {
        this.isShakeOpen = isShakeOpen;
    }
    public boolean getIsSendingEmail() {
        return this.isSendingEmail;
    }
    public void setIsSendingEmail(boolean isSendingEmail) {
        this.isSendingEmail = isSendingEmail;
    }

}
