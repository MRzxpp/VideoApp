package com.haishanda.android.videoapp.api;

import com.haishanda.android.videoapp.bean.AlarmVoBean;
import com.haishanda.android.videoapp.config.SmartResult;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 与监控相关的接口
 * Created by Zhongsz on 2016/11/22.
 */

public interface MonitorApi {

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editMonitorTime")
    Observable<SmartResult> editMonitorTime(@Field("machineId") long machineId,
                                            @Field("span") int span,
                                            @Field("begin") int begin);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editMonitorSwitch")
    Observable<SmartResult> editMonitorSwitch(@Field("machineId") long machineId,
                                              @Field("isSwitchOn") boolean isSwitchOn);

    @GET("/monitor-platform-web/rest/user/queryAlarms")
    Observable<SmartResult<List<AlarmVoBean>>> queryAlarms(@Query("lastId") int lastId);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editMonitorSms")
    Observable<SmartResult> editMonitorSms(@Field("isSwitchOn") boolean isSwitchOn);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/chooseAlarmVoice")
    Observable<SmartResult> chooseAlarmVoice(@Field("machineId") long machineId, @Field("voice") int voice);
}
