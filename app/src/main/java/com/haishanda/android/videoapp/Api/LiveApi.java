package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Bean.CameraLive;
import com.haishanda.android.videoapp.Config.SmartResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Zhongsz on 2016/11/14.
 */

public interface LiveApi {
    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/liveStart")
    Observable<SmartResult<CameraLive>> getLiveStream(@Field("cameraId") String cameraId);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/liveStop")
    Observable<SmartResult> stopLiveStream(@Field("liveId") String liveId);
}
