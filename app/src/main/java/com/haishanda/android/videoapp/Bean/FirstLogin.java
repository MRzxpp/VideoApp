package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/12/1.
 */
@Entity
public class FirstLogin {
    @Unique
    int isFirst;

    @Generated(hash = 1409485203)
    public FirstLogin(int isFirst) {
        this.isFirst = isFirst;
    }

    @Generated(hash = 656160308)
    public FirstLogin() {
    }

    public int getIsFirst() {
        return this.isFirst;
    }

    public void setIsFirst(int isFirst) {
        this.isFirst = isFirst;
    }
}
