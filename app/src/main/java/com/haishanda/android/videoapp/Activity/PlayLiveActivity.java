package com.haishanda.android.videoapp.activity;

import android.Manifest;
import android.app.Activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.media.MediaRecorder;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.bean.CameraLive;
import com.haishanda.android.videoapp.bean.VideoMessage;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.mediacontroller.LiveLandMediaController;
import com.haishanda.android.videoapp.utils.mediacontroller.LiveMediaController;
import com.haishanda.android.videoapp.utils.SaveImageToLocalUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.views.MaterialDialog;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;

import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * play live from cameras
 * Created by Zhongsz on 2016/10/19.
 */

//Todo 播放直播时横屏控制器的对讲ui问题

public class PlayLiveActivity extends Activity {
    @BindView(R.id.play_live)
    VideoView videoView;
    @BindView(R.id.vocal_is_in)
    ImageView vocalGif;
    @BindView(R.id.voice_start)
    ImageView voiceStart;
    @BindView(R.id.toggle_fullscreen)
    ImageView toggleFullscreen;
    @BindView(R.id.stop_record_btn)
    ImageView stopRecordBtn;
    @BindView(R.id.record_btn)
    ImageView recordBtn;
    @BindView(R.id.loading)
    ImageView loadingPic;
    @BindView(R.id.printscreen_btn)
    ImageView printScreenBtn;
    @BindView(R.id.back_to_boat_btn)
    ImageView backBtn;
    @BindView(R.id.play_live_title)
    RelativeLayout playLiveTitle;

    private PlayLiveActivity instance;
    private static final String TAG = "PlayLiveActivity";
    private int liveId = -1;
    private String boatName;
    public long cameraId;
    private String path;

    private MediaRecorder mRecorder;
    public String mFileNameFull;
    private FFmpeg ffmpeg;
    private String time;
    public long startTime;
    private long endTime;
    private boolean needResume;
    private long statLiveTime;
    private long endLiveTime;
    private ContentObserver rotationObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_live);
        Vitamio.isInitialized(getApplicationContext());
        ButterKnife.bind(this);
        if (instance == null) {
            synchronized (PlayLiveActivity.class) {
                if (instance == null) {
                    instance = new PlayLiveActivity();
                }
            }
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        ffmpeg = FFmpeg.getInstance(this);
        stopRecordBtn.setVisibility(View.INVISIBLE);
        stopRecordBtn.setEnabled(false);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("load ffmpeg", "start");
                }

                @Override
                public void onFailure() {
                    Log.d("load ffmpeg", "failed");
                }

                @Override
                public void onSuccess() {
                    Log.d("load ffmpeg", "success");
                }

                @Override
                public void onFinish() {
                    Log.d("load ffmpeg", "finish");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            e.printStackTrace();
        }
        //注册 Settings.System.ACCELEROMETER_ROTATION
        rotationObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                if (selfChange) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                }
            }
        };
        getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), true, rotationObserver);
        Glide.with(this)
                .load(R.drawable.voice_is_in)
                .asGif()
                .into(vocalGif);
        Glide.with(this)
                .load(R.drawable.loading)
                .asGif()
                .into(loadingPic);
        loadingPic.setVisibility(View.INVISIBLE);
        vocalGif.setVisibility(View.INVISIBLE);
        Bundle extra = getIntent().getExtras();
        cameraId = extra.getLong("cameraId");
        boatName = extra.getString("boatName");
        initTalkService();
        Thread getUrlThread = new Thread(new NetThread());
        getUrlThread.start();
        try {
            Thread.sleep(3000);
            getUrlThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //测试地址开关
        SharedPreferences preferences = getSharedPreferences("TEST_PATH", MODE_PRIVATE);
        boolean isPath = preferences.getBoolean("TEST_PATH_ON", true);
        if (isPath) {
            path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
        }
    }

    class NetThread implements Runnable {
        @Override
        public void run() {
            path = getLiveUrl((int) cameraId);
        }
    }


    @OnClick(R.id.toggle_fullscreen)
    public void toggleScreenOrientation() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "orientation changed");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            LiveLandMediaController landMediaController = new LiveLandMediaController(this, videoView, this);
            //控制器显示5s后自动隐藏
            landMediaController.show(5000);
            videoView.setMediaController(landMediaController);
            //隐藏标题栏
            playLiveTitle.setVisibility(View.GONE);
            //设置全屏即隐藏状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LiveMediaController liveMediaController = new LiveMediaController(this, videoView);
            liveMediaController.show(5000);
            videoView.setMediaController(liveMediaController);
            //恢复标题栏
            playLiveTitle.setVisibility(View.VISIBLE);
            //恢复状态栏
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoView.isPlaying()) {
            if (path != null) {
                videoView.setVideoPath(path);//设置播放地址
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    LiveMediaController liveMediaController = new LiveMediaController(this, videoView);
                    liveMediaController.show(5000);
                    videoView.setMediaController(liveMediaController);//绑定控制器
                    videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
                } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //设置全屏即隐藏状态栏
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //隐藏标题栏
                    playLiveTitle.setVisibility(View.GONE);
                    LiveLandMediaController landMediaController = new LiveLandMediaController(this, videoView, this);
                    landMediaController.show(5000);//控制器显示5s后自动隐藏
                    videoView.setMediaController(landMediaController);
                    videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
                }
                videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
                videoView.requestFocus();//取得焦点
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                //开始缓存，暂停播放
                                if (videoView.isPlaying()) {
                                    videoView.pause();
                                    needResume = true;
                                }
                                recordBtn.setEnabled(false);
                                stopRecordBtn.setEnabled(false);
                                printScreenBtn.setEnabled(false);
                                voiceStart.setEnabled(false);
                                loadingPic.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                //缓存完成，继续播放
                                if (needResume)
                                    videoView.start();
                                loadingPic.setVisibility(View.GONE);
                                recordBtn.setEnabled(true);
                                stopRecordBtn.setEnabled(true);
                                printScreenBtn.setEnabled(true);
                                voiceStart.setEnabled(true);
                                break;
                            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                                //显示 下载速度
//                                Logger.e("download rate:" + extra);
//                                Log.d(TAG, "download rate:" + extra);
                                break;
                        }
                        return true;
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
        getContentResolver().unregisterContentObserver(rotationObserver);
        instance = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //若视频地址有误时，点击退出或者截屏均会爆栈，因此通过判断videoview是否能加载来判断视频地址是否正确
            if (videoView.getCurrentFrame() != null)
                SaveImageToLocalUtil.saveCameraIconAction(videoView.getCurrentFrame(), boatName, this.cameraId);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ApiManage.getInstence().getLiveApiService().stopLiveStream(liveId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult smartResult) {
                        if (smartResult.getCode() == 1) {
                            Log.d(TAG, "停止播放，退出成功");
                            endLiveTime = System.currentTimeMillis();
                            long playTime = endLiveTime - statLiveTime;
                            Log.d(TAG, "本次播放了" + playTime / 1000 + "秒");
                        } else {
                            Log.d(TAG, smartResult.getMsg());
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @OnClick(R.id.printscreen_btn)
    public void printScreen() {
        Log.i(TAG, "printScreen");
        try {
            if (videoView.getCurrentFrame() != null) {
                SaveImageToLocalUtil.saveAction(videoView.getCurrentFrame(), boatName);
                final MaterialDialog dialog = new MaterialDialog(this);
                dialog.setMessage("截图成功！");
                Log.i("PlayVideo", " 截图成功");
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            } else {
                Toast.makeText(this, "请勿在视频有错误的情况下截图", Toast.LENGTH_LONG).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "视频还未加载完成！暂时无法截图", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.record_btn)
    public void recordVideo() {
        if (videoView.getCurrentFrame() != null) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            String path = sdcardDir.getPath() + "/VideoApp";
            File appDir = new File(path);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File boatDir = new File(appDir + "/" + boatName);
            if (!boatDir.exists()) {
                boatDir.mkdir();
            }
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
            final String date = dateFormat.format(System.currentTimeMillis());
            File dateDir = new File(boatDir + "/" + dateFormat.format(System.currentTimeMillis()));
            if (!dateDir.exists()) {
                dateDir.mkdir();
            }
            File videoDir = new File(dateDir + "/Videos");
            if (!videoDir.exists()) {
                videoDir.mkdir();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日hh:mm:ss", Locale.CHINA);
            final String time = format.format(System.currentTimeMillis());
            this.time = time;
            String cmd = "-i " + this.path + " -c copy " + videoDir.getPath() + "/" + boatName + "_" + time + ".flv";
            final String[] command = cmd.split(" ");
            try {
                // to execute "ffmpeg -version" command you just need to pass "-version"
                ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                        recordBtn.setVisibility(View.INVISIBLE);
                        recordBtn.setEnabled(false);
                        stopRecordBtn.setVisibility(View.VISIBLE);
                        stopRecordBtn.setEnabled(true);
                        VideoMessageDao videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
                        VideoMessage videoMessage = new VideoMessage(null, boatName, boatName + "_" + time + ".flv", time, date, null);
                        videoMessageDao.insertOrReplace(videoMessage);
                        Log.d(TAG, "Started command : ffmpeg ");
                    }

                    @Override
                    public void onProgress(String message) {
                        Log.d(TAG, "Progress command : ffmpeg ");
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.d(TAG, "ffmpeg failure" + message);
                    }

                    @Override
                    public void onSuccess(String message) {
                        Log.d(TAG, "ffmpeg success" + message);
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "ffmpeg finish");
                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                // Handle if FFmpeg is already running
            }
        } else {
            Toast.makeText(this, "请勿在视频有错误的情况下录像", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.stop_record_btn)
    public void terminateRecord() {
        stopRecordBtn.setVisibility(View.INVISIBLE);
        stopRecordBtn.setEnabled(false);
        recordBtn.setVisibility(View.VISIBLE);
        recordBtn.setEnabled(true);
        if (videoView.getCurrentFrame() != null) {
            SaveImageToLocalUtil.saveVideoIconAction(videoView.getCurrentFrame(), boatName, time);
        }
        if (ffmpeg.isFFmpegCommandRunning()) {
            ffmpeg.killRunningProcesses();
            Log.d(TAG, "record success");
        }
        Log.d(TAG, "record finish");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "录音权限开启");
            } else {
                // Permission Denied
                Toast.makeText(instance, "您拒绝开启对讲功能", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void onTalkStart() {
        voiceStart.setImageResource(R.drawable.interphone_pick);
        vocalGif.setVisibility(View.VISIBLE);
        backBtn.setEnabled(false);
        toggleFullscreen.setEnabled(false);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                startRecordVoice();
//            }
//        }).start();
        startRecordVoice();

    }

    public void initTalkService() {
        voiceStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(instance, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                        } else {
                            onTalkStart();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED) {
                            voiceStart.setEnabled(false);
                            vocalGif.setVisibility(View.INVISIBLE);
                            try {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopRecordVoice();
                                    }
                                }).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "record voice failed");
                            }
                            voiceStart.setEnabled(true);
                            backBtn.setEnabled(true);
                            toggleFullscreen.setEnabled(true);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED) {
                            voiceStart.setEnabled(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    stopVoiceRecorder();
                                    File voiceFile = new File(mFileNameFull);
                                    try {
                                        voiceFile.delete();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            voiceStart.setEnabled(true);
                            backBtn.setEnabled(true);
                            toggleFullscreen.setEnabled(true);
                            Log.d(TAG, "record voice cancel");
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void stopVoiceRecorder() {
        endTime = System.currentTimeMillis();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                voiceStart.setImageResource(R.drawable.interphone);
            }
        });
        if (mRecorder != null) {
            try {
                //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                //报错为：RuntimeException:stop failed
                mRecorder.setOnErrorListener(null);
                mRecorder.setOnInfoListener(null);
                mRecorder.setPreviewDisplay(null);
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                Log.d(TAG, "record voice finish");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void startRecordVoice() {
        Log.d(TAG, "record voice start");
        startTime = System.currentTimeMillis();
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Log.i(TAG, "SD Card is not mounted,It is  " + state + ".");
        }
        File sdcardDir = Environment.getExternalStorageDirectory();
        String path = sdcardDir.getPath() + "/VideoApp";
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File boatDir = new File(appDir + "/" + boatName);
        if (!boatDir.exists()) {
            boatDir.mkdir();
        }
        File voiceDir = new File(boatDir + "/Voices");
        if (!voiceDir.exists()) {
            voiceDir.mkdir();
        }
        String mFileName = UUID.randomUUID().toString() + ".amr";
        mFileNameFull = voiceDir + "/" + mFileName;
        mRecorder = new MediaRecorder();
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mRecorder.setOutputFile(mFileNameFull);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "start voice record failed");
        }
    }

    public void stopRecordVoice() {
        stopVoiceRecorder();
        Thread voiceThread = new SendVoiceThread();
        voiceThread.start();
    }

    class SendVoiceThread extends Thread {
        @Override
        public void run() {
            synchronized (this) {
                Looper.prepare();
                long voiceTime = endTime - startTime;
                try {
                    File voiceFile = new File(mFileNameFull);
                    int MIN_VOICE_TIME = 2000;
                    if (voiceTime < MIN_VOICE_TIME) {
                        try {
                            voiceFile.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "发送录音中");
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), voiceFile);
                        // MultipartBody.Part is used to send also the actual filename
                        MultipartBody.Part body = MultipartBody.Part.createFormData("voice", voiceFile.getName(), requestFile);
                        MultipartBody.Part machineId = MultipartBody.Part.createFormData("cameraId", String.valueOf(cameraId));
                        ApiManage.getInstence().getBoatApiService().uploadVoice(body, machineId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<SmartResult>() {
                                    @Override
                                    public void onCompleted() {
                                        android.util.Log.d("上传录音", "completed");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        android.util.Log.d("上传录音", "error");
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(SmartResult smartResult) {
                                        if (smartResult.getCode() == 1) {
                                            android.util.Log.d("上传录音", "success");
                                        } else {
                                            android.util.Log.d("上传录音", "failed");
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.back_to_boat_btn)
    public void backToBoat(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    public String getLiveUrl(int cameraId) {
        final String[] liveUrlCopy = new String[1];
        final int[] liveIdCopy = new int[1];
        Call<SmartResult<CameraLive>> call = ApiManage.getInstence().getLiveApiService().getLiveStreamCopy(cameraId);
        try {
            Response<SmartResult<CameraLive>> response = call.execute();
            if (response.body().getCode() == 1) {
                statLiveTime = System.currentTimeMillis();
                CameraLive cameraLive = response.body().getData();
                String liveUrl = "default";
                int liveId = -1;
                if (cameraLive != null) {
                    if (cameraLive.getLiveUrl() != null) {
                        liveUrl = cameraLive.getLiveUrl();
                    }
                    if (cameraLive.getLiveId() != 0) {
                        liveId = cameraLive.getLiveId();
                    }
                }
                liveUrlCopy[0] = liveUrl;
                liveIdCopy[0] = liveId;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.liveId = liveIdCopy[0];
        return liveUrlCopy[0];
    }

}
