package com.haishanda.android.videoapp.bean;

/**
 * 每个摄像头所对应的播放地址
 * Created by Zhongsz on 2016/11/14.
 */

public class CameraLive {
    private int liveId;
    private String liveUrl;

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }
}
