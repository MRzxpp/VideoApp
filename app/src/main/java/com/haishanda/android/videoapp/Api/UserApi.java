package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Bean.RegisterBean;
import com.haishanda.android.videoapp.Bean.UserBean;
import com.haishanda.android.videoapp.Bean.VideoMessage;
import com.haishanda.android.videoapp.Config.SmartResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public interface UserApi {
    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/fetchCode")
    Observable<SmartResult> getFetchCode(@Field("mobileNo") String mobileNo);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/regist")
    Observable<SmartResult> signupAction(@Field("mobileNo") String mobileNo,
                                         @Field("password") String password,
                                         @Field("code") String code);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/userLogin")
    Observable<SmartResult<UserBean>> loginAction(@Field("name") String username,
                                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/userLogin")
    Call<SmartResult<UserBean>> loginActionCopy(@Field("name") String username,
                                                @Field("password") String password);


}
