package com.haishanda.android.videoapp.Api;

import com.haishanda.android.videoapp.Config.Config;
import com.haishanda.android.videoapp.Utils.NullOnEmptyConverterFactory;
import com.haishanda.android.videoapp.VideoApplication;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    private UserApi userApi;
    private BoatApi boatApi;
    private LiveApi liveApi;
    private MonitorApi monitorApi;

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

    public UserApi getUserApiServiceWithToken() {
        if (userApi == null) {
            synchronized (zhihuMonitor) {
                if (userApi == null) {
                    userApi = new Retrofit.Builder()
                            .baseUrl(Config.SERVER_HOME)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(new NullOnEmptyConverterFactory())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(genericClient())
                            .build()
                            .create(UserApi.class);
                }
            }
        }
        return userApi;
    }

    public LiveApi getLiveApiService() {
        if (liveApi == null) {
            synchronized (zhihuMonitor) {
                if (liveApi == null) {
                    liveApi = new Retrofit.Builder()
                            .baseUrl(Config.SERVER_HOME)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(genericClient())
                            .build()
                            .create(LiveApi.class);

                }
            }
        }
        return liveApi;
    }

    public BoatApi getBoatApiService() {
        if (boatApi == null) {
            synchronized (zhihuMonitor) {
                if (boatApi == null) {
                    boatApi = new Retrofit.Builder()
                            .baseUrl(Config.SERVER_HOME)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(genericClient())
                            .build()
                            .create(BoatApi.class);

                }
            }
        }
        return boatApi;
    }

    public MonitorApi getMonitorApiService() {
        if (monitorApi == null) {
            synchronized (zhihuMonitor) {
                if (monitorApi == null) {
                    monitorApi = new Retrofit.Builder()
                            .baseUrl(Config.SERVER_HOME)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(genericClient())
                            .build()
                            .create(MonitorApi.class);

                }
            }
        }
        return monitorApi;
    }

    private OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/json;charset=UTF-8")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "*/*")
                                .addHeader("token", VideoApplication.getApplication().getToken())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .retryOnConnectionFailure(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
        return httpClient;
    }

}
