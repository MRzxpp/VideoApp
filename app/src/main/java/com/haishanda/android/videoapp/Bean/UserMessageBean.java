package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 登录后的头像与昵称信息
 * Created by Zhongsz on 2016/11/26.
 */
@Entity
public class UserMessageBean {
    private String nickName;
    private String portraitUrl;
    @Index
    @Id(autoincrement = false)
    private long id;

    @Generated(hash = 113648068)
    public UserMessageBean(String nickName, String portraitUrl, long id) {
        this.nickName = nickName;
        this.portraitUrl = portraitUrl;
        this.id = id;
    }

    @Generated(hash = 1022199535)
    public UserMessageBean() {
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortraitUrl() {
        return this.portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
