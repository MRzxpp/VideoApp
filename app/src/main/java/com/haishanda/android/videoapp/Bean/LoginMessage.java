package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/11/21.
 */
@Entity
public class LoginMessage {
    @Index(unique = true)
    private String username;
    @Index(unique = true)
    private String password;
    @Generated(hash = 1481530845)
    public LoginMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Generated(hash = 2079515228)
    public LoginMessage() {
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
