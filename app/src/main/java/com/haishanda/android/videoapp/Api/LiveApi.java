package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Bean.CameraLive;
import com.haishanda.android.videoapp.Config.SmartResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 与摄像头相关的接口
 * Created by Zhongsz on 2016/11/14.
 */

public interface LiveApi {
    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/liveStart")
    Observable<SmartResult<CameraLive>> getLiveStream(@Field("cameraId") int cameraId);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/liveStart")
    Call<SmartResult<CameraLive>> getLiveStreamCopy(@Field("cameraId") int cameraId);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/liveStop")
    Observable<SmartResult> stopLiveStream(@Field("liveId") int liveId);
}
