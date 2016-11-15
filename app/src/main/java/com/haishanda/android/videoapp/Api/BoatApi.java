package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Config.SmartResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Zhongsz on 2016/11/14.
 */

public interface BoatApi {
    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/bondModule")
    Observable<SmartResult> addBoat(@Field("globalId") String globalId,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/resetModulePassword")
    Observable<SmartResult> resetBoatPassword(@Field("machineId") String machineId);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/removeMachine")
    Observable<SmartResult> removeBoat(@Field("machineId") String machineId,
                                       @Field("password") String password);


}
