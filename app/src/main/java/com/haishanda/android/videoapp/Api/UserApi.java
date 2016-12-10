package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Bean.PackageVo;
import com.haishanda.android.videoapp.Bean.UserBean;
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
import rx.Observable;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public interface UserApi {
    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/fetchCode")
    Observable<SmartResult> getFetchCode(@Field("mobileNo") String mobileNo);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/fetchResetCode")
    Observable<SmartResult> getResetCode(@Field("mobileNo") String mobileNo);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/validateCode")
    Observable<SmartResult> validateResetCode(@Field("mobileNo") String mobileNo,
                                              @Field("code") String code);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/resetPassword")
    Observable<SmartResult> forgetPassword(@Field("mobileNo") String mobileNo,
                                           @Field("password") String password);

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

    @POST("/monitor-platform-web/rest/user/fetchCode")
    Observable<SmartResult> getFetchCodeWithToken(@Header("token") String token);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/validateCode")
    Observable<SmartResult> validateCode(@Field("code") String code);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/resetPassword")
    Observable<SmartResult> resetPassword(@Field("password") String password);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editNickName")
    Observable<SmartResult> editNickName(@Header("token") String token, @Field("nickName") String nickName);

    @Multipart
    @POST("/monitor-platform-web/rest/user/uploadPortrait")
    Observable<SmartResult<String>> uploadPortrait(@Header("token") String token, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("/monitor-platform-web/rest/user/editPortrait")
    Observable<SmartResult> editPortrait(@Header("token") String token, @Field("portrait") String portrait);

    @GET("/monitor-platform-web/rest/user/queryPackages")
    Call<SmartResult<List<PackageVo>>> queryPackage(@Header("token") String token);

}
