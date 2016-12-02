package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/12/1.
 */
@Entity
public class LastId {
    @Unique
    private int lastId;

    @Generated(hash = 312737873)
    public LastId(int lastId) {
        this.lastId = lastId;
    }

    @Generated(hash = 1600793868)
    public LastId() {
    }

    public int getLastId() {
        return this.lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }
}
