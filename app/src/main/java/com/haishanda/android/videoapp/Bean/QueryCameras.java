package com.haishanda.android.videoapp.Bean;

/**
 * 储存摄像头信息
 * Created by Zhongsz on 2016/11/15.
 */

public class QueryCameras {

    /**
     * id : 2
     * owner : 2
     * icon : null
     * status : 0
     */

    private long id;
    private int owner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
}
