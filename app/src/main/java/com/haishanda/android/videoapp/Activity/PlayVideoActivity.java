package com.haishanda.android.videoapp.Activity;

import android.app.Activity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.CameraLive;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.Bean.QueryCameras;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.CustomMediaController;
import com.haishanda.android.videoapp.Utils.DownloadUtil;
import com.haishanda.android.videoapp.Utils.SaveImageToLocalUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;

import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Url;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/10/19.
 */

public class PlayVideoActivity extends Activity {
    @BindView(R.id.test_play_video)
    VideoView videoView;
    @BindView(R.id.test_print)
    ImageView testPrint;

    private static final String TAG = "PlayVideoActivity";
    private ImageMessageDao imageMessageDao;
    private CustomMediaController mCustomMediaController;
    private Bundle extra;
    private int liveId = -1;
    private String boatName;
    private long cameraId;
    private String path;
    private DownloadUtil l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        Vitamio.isInitialized(getApplicationContext());
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        cameraId = extra.getLong("cameraId");
        boatName = extra.getString("boatName");
//        String path = getLiveUrl(cameraId);
        path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!videoView.isPlaying()) {
            if (path != null) {
//        String path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
                videoView.setVideoPath(path);//设置播放地址
                MediaController mMediaController = new MediaController(this);
                mCustomMediaController = new CustomMediaController(this, videoView, this);
                mMediaController.show(5000);//控制器显示5s后自动隐藏
                videoView.setMediaController(mCustomMediaController);//绑定控制器
                videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
                videoView.requestFocus();//取得焦点
            } else {
                path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
                videoView.setVideoPath(path);//设置播放地址
                MediaController mMediaController = new MediaController(this);
                mCustomMediaController = new CustomMediaController(this, videoView, this);
                mMediaController.show(5000);//控制器显示5s后自动隐藏
                videoView.setMediaController(mCustomMediaController);//绑定控制器
                videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
                videoView.requestFocus();//取得焦点
            }
        } else {
            videoView.start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        super.onPause();
        try {
            SaveImageToLocalUtil.saveCameraIconAction(videoView.getCurrentFrame(), boatName, this.cameraId);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        videoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ApiManage.getInstence().getLiveApiService().stopLiveStream(liveId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            Log.i(TAG, "停止播放，退出成功");
                        } else {
                            Log.i(TAG, smartResult.getMsg());
                        }
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.printscreen_btn)
    public void printScreen(View view) {
        Log.i(TAG, "printScreen");
        try {
            SaveImageToLocalUtil.saveAction(videoView.getCurrentFrame(), boatName);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "视频还未加载完成！暂时无法截图", Toast.LENGTH_LONG).show();
        }

        Log.i("PlayVideo", " 截图成功");
    }

    @OnClick(R.id.record_btn)
    public void recordVideo(View view) {
        Log.i(TAG, "recordVideo");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //这里就一条消息
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //另起线程执行下载，安卓最新sdk规范，网络操作不能再主线程。
                l = new DownloadUtil(path);

                /**
                 * 下载文件到sd卡，虚拟设备必须要开始设置sd卡容量
                 * downhandler是Download的内部类，作为回调接口实时显示下载数据
                 */
                int status = l.down2sd("downtemp/", "test.flv", l.new downhandler() {
                    @Override
                    public void setSize(int size) {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = size;
                        msg.sendToTarget();
                        Log.d("log", Integer.toString(size));
                    }
                });
                //log输出
                Log.d("log", Integer.toString(status));

            }
        }).start();
    }

    @OnClick(R.id.interphone_btn)
    public void talkService() {
        l.getUrlcon().disconnect();
    }

    @OnClick(R.id.back_to_boat_btn)
    public void backToBoat(View view) {
        this.finish();
    }

    public String getLiveUrl(int cameraId) {
        final String[] liveUrlCopy = new String[1];
        final int[] liveIdCopy = new int[1];
        Call<SmartResult<CameraLive>> call = ApiManage.getInstence().getLiveApiService().getLiveStreamCopy(cameraId);
        try {
            Response<SmartResult<CameraLive>> response = call.execute();
            CameraLive cameraLive = response.body().getData();
            String liveUrl = cameraLive.getLiveUrl();
            int liveId = cameraLive.getLiveId();
            liveUrlCopy[0] = liveUrl;
            liveIdCopy[0] = liveId;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "请重新登录", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PlayVideoActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        this.liveId = liveIdCopy[0];
        return liveUrlCopy[0];
    }


}
