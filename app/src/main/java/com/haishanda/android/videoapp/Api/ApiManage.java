package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Config.Config;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public class ApiManage {

    private static ApiManage apiManage;
    private Object zhihuMonitor = new Object();

    public static ApiManage getInstence() {
        if (apiManage == null) {
            synchronized (ApiManage.class) {
                if (apiManage == null) {
                    apiManage = new ApiManage();
                }
            }
        }
        return apiManage;
    }

    private VideoApi videoApi;
    private UserApi userApi;


    public VideoApi getVideoApiService() {
        if (videoApi == null) {
            synchronized (zhihuMonitor) {
                if (videoApi == null) {
                    videoApi = new Retrofit.Builder()
                            .baseUrl(Config.SERVER_HOME)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(VideoApi.class);

                }
            }
        }
        return videoApi;
    }

    public UserApi getUserApiService() {
        if (userApi == null) {
            synchronized (zhihuMonitor) {
                if (userApi == null) {
                    userApi = new Retrofit.Builder()
                            .baseUrl(Config.SERVER_HOME)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(UserApi.class);
                }
            }
        }
        return userApi;
    }
}
