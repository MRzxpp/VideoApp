package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Config.SmartResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Zhongsz on 2016/11/22.
 */

public interface MonitorApi {

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editMonitorTime")
    Observable<SmartResult> editMonitorTime(@Field("machineId") int machineId,
                                            @Field("span") int span,
                                            @Field("begin") int begin);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editMonitorSwitch")
    Observable<SmartResult> editMonitorSwitch (@Field("machineId") int machineId,
                                               @Field("isSwitch") boolean isSwitch);
}
