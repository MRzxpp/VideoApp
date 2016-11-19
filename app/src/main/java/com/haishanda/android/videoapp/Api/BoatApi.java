package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Bean.QueryCameras;
import com.haishanda.android.videoapp.Bean.QueryMachines;
import com.haishanda.android.videoapp.Config.SmartResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
    Observable<SmartResult> resetBoatPassword(@Field("machineId") int machineId);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/removeMachine")
    Observable<SmartResult> removeBoat(@Field("machineId") String machineId,
                                       @Field("password") String password);

    @GET("monitor-platform-web/rest/user/queryMachines")
    Observable<SmartResult<List<QueryMachines>>> queryMachines();

    @GET("monitor-platform-web/rest/user/queryMachines")
    Call<SmartResult<List<QueryMachines>>> queryMachinesCopy();

    @GET("monitor-platform-web/rest/user/queryCameras")
    Observable<SmartResult<List<QueryCameras>>> queryCameras(@Query("machineId") int machineId);

    @GET("monitor-platform-web/rest/user/queryCameras")
    Call<SmartResult<List<QueryCameras>>> queryCamerasCopy(@Query("machineId") int machineId);

}
