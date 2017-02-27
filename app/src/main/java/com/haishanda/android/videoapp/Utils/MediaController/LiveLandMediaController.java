package com.haishanda.android.videoapp.utils.mediacontroller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.activity.PlayLiveActivity;
import com.haishanda.android.videoapp.R;

import java.io.File;
import java.lang.ref.WeakReference;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 直播界面下的media controller，包含有对讲，截屏和录像的功能
 * Created by Zhongsz on 2016/11/2.
 */

public class LiveLandMediaController extends MediaController {
    private static final int HIDEFRAM = 0;//控制提示窗口的显示
    private static final String TAG = "LiveController";

    private LiveLandMediaController controller;
    private GestureDetector mGestureDetector;
    private VideoView videoView;
    private PlayLiveActivity activity;
    private Context context;
    private int controllerWidth = 0;//设置mediaController高度为了使横屏时top显示在屏幕顶端

    private View mVolumeBrightnessLayout;//提示窗口
    private ImageView mOperationBg;//提示图片
    private ImageView mRecordVideoBtn;
    private ImageView mStopRecordVideoBtn;
    private ImageView backBtn;
    private ImageView toggleFullscrrenBtn;
    private TextView mOperationTv;//提示文字
    private ImageView volumeToggle;
    private ImageView playOrPauseBtn;
    private AudioManager mAudioManager;
    //最大声音
    private int mMaxVolume;
    // 当前声音
    private int mVolume = -1;
    private int volumnState = 0;
    //当前亮度
    private float mBrightness = -1f;

    public LiveLandMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //返回监听
    private final View.OnClickListener backListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (activity != null) {
                activity.finish();
            }
        }
    };

    private final View.OnClickListener volumnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume != 0) {
                volumnState = mVolume;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                volumeToggle.setImageResource(R.drawable.volumn_off);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumnState, 0);
                volumeToggle.setImageResource(R.drawable.volumn_on);
            }
        }
    };

    private final OnClickListener playOrPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            playOrPause();
        }
    };

    private static class LiveLandHandler extends Handler {
        private final WeakReference<LiveLandMediaController> controllerWeakReference;

        LiveLandHandler(LiveLandMediaController controller) {
            controllerWeakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            LiveLandMediaController liveLandMediaController = controllerWeakReference.get();
            if (liveLandMediaController != null) {
                switch (msg.what) {
                    case HIDEFRAM://隐藏提示窗口
                        liveLandMediaController.mVolumeBrightnessLayout.setVisibility(View.GONE);
                        liveLandMediaController.mOperationTv.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    private final LiveLandHandler liveLandHandler = new LiveLandHandler(this);

    public LiveLandMediaController(Context context, VideoView videoView, PlayLiveActivity activity) {
        super(context);
        this.context = context;
        this.videoView = videoView;
        this.activity = activity;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        controllerWidth = point.x;
        if (controller == null) {
            controller = this;
        }
    }

    @Override
    protected View makeControllerView() {
        //此处的   media_controller_live_land  为我们自定义控制器的布局文件名称
        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("media_controller_live_land", "layout", getContext().getPackageName()), this);
        v.setMinimumHeight(controllerWidth);
        //获取控件
        volumeToggle = (ImageView) v.findViewById(R.id.toggle_volume);
        ImageView mPrintScreenBtn = (ImageView) v.findViewById(R.id.printscreen_btn);
        mRecordVideoBtn = (ImageView) v.findViewById(R.id.record_btn);
        mStopRecordVideoBtn = (ImageView) v.findViewById(R.id.stop_record_btn);
        ImageView mTalkServiceBtn = (ImageView) v.findViewById(R.id.voice_start);
        backBtn = (ImageView) v.findViewById(R.id.back_to_boat_btn);
        toggleFullscrrenBtn = (ImageView) v.findViewById(R.id.toggle_fullscreen);
        playOrPauseBtn = (ImageView) v.findViewById(R.id.media_controller_play_pause);
        TextView cameraTitle = (TextView) v.findViewById(R.id.camera_title);
        cameraTitle.setText("摄像头" + activity.cameraId);
        //声音控制
        mVolumeBrightnessLayout = v.findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) v.findViewById(R.id.operation_bg);
        mOperationTv = (TextView) v.findViewById(R.id.operation_tv);
        mOperationTv.setVisibility(View.GONE);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (mVolume == 0) {
            volumeToggle.setImageResource(R.drawable.volumn_off);
        }
        //注册事件监听
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
        playOrPauseBtn.setOnClickListener(playOrPauseListener);
        volumeToggle.setOnClickListener(volumnListener);
        backBtn.setOnClickListener(backListener);
        mPrintScreenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.printScreen();
            }
        });
        //录音键
        mTalkServiceBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                activity.startRecordVoice();
                                controller.show(3600000);
                            }
                        }).start();
                        backBtn.setEnabled(false);
                        toggleFullscrrenBtn.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.stopRecordVoice();
                                }
                            }).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "record voice failed");
                        }
                        backBtn.setEnabled(true);
                        toggleFullscrrenBtn.setEnabled(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                activity.stopVoiceRecorder();
                                File voiceFile = new File(activity.mFileNameFull);
                                try {
                                    voiceFile.delete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        backBtn.setEnabled(true);
                        toggleFullscrrenBtn.setEnabled(true);
                        Log.d(TAG, "record voice cancel");
                    default:
                        break;
                }
                return true;
            }
        });
        //全屏键
        toggleFullscrrenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
        //录像键
        mRecordVideoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.recordVideo();
                mRecordVideoBtn.setVisibility(View.INVISIBLE);
                mRecordVideoBtn.setEnabled(false);
                mStopRecordVideoBtn.setVisibility(View.VISIBLE);
                mStopRecordVideoBtn.setEnabled(true);
            }
        });
        mStopRecordVideoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.terminateRecord();
                mRecordVideoBtn.setVisibility(View.VISIBLE);
                mRecordVideoBtn.setEnabled(true);
                mStopRecordVideoBtn.setVisibility(View.INVISIBLE);
                mStopRecordVideoBtn.setEnabled(false);
            }
        });
        return v;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println("MYApp-MyMediaController-dispatchKeyEvent");
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) return true;
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        // 隐藏
        liveLandHandler.removeMessages(HIDEFRAM);
        liveLandHandler.sendEmptyMessageDelayed(HIDEFRAM, 1);

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        /**
         * 因为使用的是自定义的mediaController 当显示后，mediaController会铺满屏幕，
         * 所以VideoView的点击事件会被拦截，所以重写控制器的手势事件，
         * 将全部的操作全部写在控制器中，
         * 因为点击事件被控制器拦截，无法传递到下层的VideoView，
         * 所以 原来的单机隐藏会失效，作为代替，
         * 在手势监听中onSingleTapConfirmed（）添加自定义的隐藏/显示，
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //当手势结束，并且是单击结束时，控制器隐藏/显示
            toggleMediaControlsVisiblity();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        //滑动事件监听
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();
            Display disp = activity.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            disp.getSize(point);
            int windowWidth = point.x;
            int windowHeight = point.y;
            if (mOldX > windowWidth * 3.0 / 4.0) {// 右边滑动 屏幕 3/4
                onVolumeSlide((mOldY - y) / windowHeight);
            } else if (mOldX < windowWidth * 1.0 / 4.0) {// 左边滑动 屏幕 1/4
                onBrightnessSlide((mOldY - y) / windowHeight);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            playOrPause();
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    /**
     * 滑动改变声音大小
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
            mOperationTv.setVisibility(VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        if (index >= 10) {
            mOperationBg.setImageResource(R.drawable.volumn_3);
        } else if (index >= 5 && index < 10) {
            mOperationBg.setImageResource(R.drawable.volumn_2);
        } else if (index > 0 && index < 5) {
            mOperationBg.setImageResource(R.drawable.volumn_1);
        } else {
            mOperationBg.setImageResource(R.drawable.volumn_0);
        }
        //DecimalFormat    df   = new DecimalFormat("######0.00");
        mOperationTv.setText((int) (((double) index / mMaxVolume) * 100) + "%");
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

    }

    /**
     * 滑动改变亮度
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = activity.getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
            mOperationTv.setVisibility(VISIBLE);

        }


        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        activity.getWindow().setAttributes(lpa);

        mOperationTv.setText((int) (lpa.screenBrightness * 100) + "%");
        if (lpa.screenBrightness * 100 >= 87.5) {
            mOperationBg.setImageResource(R.drawable.bright_8);
        } else if (lpa.screenBrightness * 100 >= 75 && lpa.screenBrightness * 100 < 87.5) {
            mOperationBg.setImageResource(R.drawable.bright_7);
        } else if (lpa.screenBrightness * 100 >= 62.5 && lpa.screenBrightness * 100 < 75) {
            mOperationBg.setImageResource(R.drawable.bright_6);
        } else if (lpa.screenBrightness * 100 >= 50 && lpa.screenBrightness * 100 < 62.5) {
            mOperationBg.setImageResource(R.drawable.bright_5);
        } else if (lpa.screenBrightness * 100 >= 37.5 && lpa.screenBrightness * 100 < 50) {
            mOperationBg.setImageResource(R.drawable.bright_4);
        } else if (lpa.screenBrightness * 100 >= 25 && lpa.screenBrightness * 100 < 37.5) {
            mOperationBg.setImageResource(R.drawable.bright_3);
        } else if (lpa.screenBrightness * 100 >= 12.5 && lpa.screenBrightness * 100 < 25) {
            mOperationBg.setImageResource(R.drawable.bright_2);
        } else if (lpa.screenBrightness * 100 >= 0 && lpa.screenBrightness * 100 < 12.5) {
            mOperationBg.setImageResource(R.drawable.bright_1);
        }

    }


    /**
     * 设置视频文件名
     *
     * @param name
     */
//    public void setVideoName(String name) {
//        videoname = name;
//        if (mFileName != null) {
//            mFileName.setText(name);
//        }
//    }

    /**
     * 隐藏或显示
     */
    private void toggleMediaControlsVisiblity() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    /**
     * 播放/暂停
     */
    private void playOrPause() {
        if (videoView != null)
            if (videoView.isPlaying()) {
                videoView.pause();
                playOrPauseBtn.setImageResource(R.drawable.play_video);
            } else {
                videoView.start();
                playOrPauseBtn.setImageResource(R.drawable.stop_video);
            }
    }
}
