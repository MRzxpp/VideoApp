package com.haishanda.android.videoapp.Activity;

import android.app.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.CameraLive;
import com.haishanda.android.videoapp.Bean.VideoMessage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.CustomLandMediaController;
import com.haishanda.android.videoapp.Utils.CustomMediaController;
import com.haishanda.android.videoapp.Utils.SaveImageToLocalUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.Views.MaterialDialog;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;


import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    private static final String TAG = "PlayLiveActivity";
    private CustomMediaController mCustomMediaController;
    private int liveId = -1;
    private String boatName;
    private long cameraId;
    private String path;

    private MediaRecorder mRecorder;
    private String mFileNameFull;
    private FFmpeg ffmpeg;
    private String time;
    private long startTime;
    private boolean needResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_live);
        Log.d(TAG, "create");
        Vitamio.isInitialized(getApplicationContext());
        ButterKnife.bind(this);
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
//        //全屏键不可用
//        toggleFullscreen.setVisibility(View.INVISIBLE);
//        toggleFullscreen.setEnabled(false);
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
            getUrlThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
//        path = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
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
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        Log.d(TAG, "orientation changed");
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            mCustomMediaController = new CustomMediaController(this, videoView, this);
//            mCustomMediaController.show(5000);
//            videoView.setMediaController(mCustomMediaController);
//            // land donothing is ok
//        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            CustomLandMediaController landMediaController = new CustomLandMediaController(this, videoView, this);
//            landMediaController.show(5000);//控制器显示5s后自动隐藏
//            videoView.setMediaController(landMediaController);
//        }
//        super.onConfigurationChanged(newConfig);
//    }

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
                    mCustomMediaController = new CustomMediaController(this, videoView, this);
                    mCustomMediaController.show(5000);
                    videoView.setMediaController(mCustomMediaController);//绑定控制器
                } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    CustomLandMediaController landMediaController = new CustomLandMediaController(this, videoView, this);
                    landMediaController.show(5000);//控制器显示5s后自动隐藏
                    videoView.setMediaController(landMediaController);
                }
                videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);//设定缩放参数
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
                                loadingPic.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                //缓存完成，继续播放
                                if (needResume)
                                    videoView.start();
                                loadingPic.setVisibility(View.GONE);
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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.printscreen_btn)
    public void printScreen(View view) {
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
    public void recordVideo(View view) {
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
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            final String date = dateFormat.format(System.currentTimeMillis());
            File dateDir = new File(boatDir + "/" + dateFormat.format(System.currentTimeMillis()));
            if (!dateDir.exists()) {
                dateDir.mkdir();
            }
            File videoDir = new File(dateDir + "/Videos");
            if (!videoDir.exists()) {
                videoDir.mkdir();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日hh:mm:ss");
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
                        Log.d(TAG, "Started command : ffmpeg " + command);
                    }

                    @Override
                    public void onProgress(String message) {
                        Log.d(TAG, "Progress command : ffmpeg " + command);
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

    public void initTalkService() {
        voiceStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        voiceStart.setImageResource(R.drawable.interphone_pick);
                        vocalGif.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                startVoice();
                            }
                        }).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopVoice();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        stopVoiceRecord();
                        Toast.makeText(getApplicationContext(), "取消录音", Toast.LENGTH_LONG).show();
                        File voiceFile = new File(mFileNameFull);
                        voiceFile.delete();
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void stopVoiceRecord() {
        voiceStart.setImageResource(R.drawable.interphone);
        vocalGif.setVisibility(View.INVISIBLE);
        if (mRecorder != null) {
            try {
                //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                //报错为：RuntimeException:stop failed
                mRecorder.setOnErrorListener(null);
                mRecorder.setOnInfoListener(null);
                mRecorder.setPreviewDisplay(null);
                mRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO: handle exception
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
                // TODO: handle exception
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            Log.d(TAG, "record voice finished");
        }
    }

    private void startVoice() {
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
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(mFileNameFull);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    private void stopVoice() {
        stopVoiceRecord();
        Thread voiceThread = new SendVoiceThread();
        voiceThread.start();
    }

    class SendVoiceThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            long voiceTime = System.currentTimeMillis() - startTime;
            File voiceFile = new File(mFileNameFull);
            int MIN_VOICE_TIME = 3000;
            if (voiceTime < MIN_VOICE_TIME) {
                Toast.makeText(getApplicationContext(), "时间太短", Toast.LENGTH_LONG).show();
                voiceFile.delete();
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
                                    Toast.makeText(getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : "对讲失败", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "请重新登录", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PlayLiveActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        this.liveId = liveIdCopy[0];
        return liveUrlCopy[0];
    }

    public int getHeightPixel(Activity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }

    public int getWidthPixel(Activity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    public int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;
        return statusBarHeight;
    }


    public int getTitleHeight(Activity activity) {
        View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);///获得根视图
        int titleHeight = v.getTop() - getStatusBarHeight(activity);//v.getTop():状态栏标题栏的总高度 ,   所以标题栏的高度为v.getTop()-getStatusBarHeight()
        return titleHeight;
    }


}
