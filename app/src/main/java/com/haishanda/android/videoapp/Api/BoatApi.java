package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Bean.QueryCameras;
import com.haishanda.android.videoapp.Bean.QueryMachines;
import com.haishanda.android.videoapp.Config.SmartResult;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    Observable<SmartResult> removeBoat(@Field("machineId") int machineId,
                                       @Field("password") String password);

    @GET("monitor-platform-web/rest/user/queryMachines")
    Observable<SmartResult<List<QueryMachines>>> queryMachines();

    @GET("monitor-platform-web/rest/user/queryMachines")
    Call<SmartResult<List<QueryMachines>>> queryMachinesCopy();

    @GET("monitor-platform-web/rest/user/queryCameras")
    Observable<SmartResult<List<QueryCameras>>> queryCameras(@Query("machineId") int machineId);

    @GET("monitor-platform-web/rest/user/queryCameras")
    Call<SmartResult<List<QueryCameras>>> queryCamerasCopy(@Query("machineId") int machineId);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editMachineName")
    Observable<SmartResult> editMachineName(@Field("machineId") int machineId,
                                            @Field("name") String boatName);

    @Multipart
    @POST("/monitor-platform-web/rest/user/uploadVoice")
    Observable<SmartResult> uploadVoice(@Part MultipartBody.Part voice, @Part MultipartBody.Part cameraId);


}
