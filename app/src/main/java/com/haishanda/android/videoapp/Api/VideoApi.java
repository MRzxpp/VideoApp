package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Bean.VideoMessage;

import retrofit2.http.GET;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public interface VideoApi {
    @GET("api/video/message")
    VideoMessage getVideoMessage();
}
