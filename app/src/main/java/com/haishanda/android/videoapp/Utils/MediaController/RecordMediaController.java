package com.haishanda.android.videoapp.Utils.MediaController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.haishanda.android.videoapp.Activity.PlayRecordActivity;
import com.haishanda.android.videoapp.R;

import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 竖屏时的视频控件控制器
 * Created by Zhongsz on 2016/11/2.
 */

public class RecordMediaController extends MediaController {
    private static final int HIDEFRAM = 0;//控制提示窗口的显示
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private GestureDetector mGestureDetector;
    private VideoView videoView;
    private PlayRecordActivity activity;
    private Context context;
    private int controllerWidth = 0;//设置mediaController高度为了使横屏时top显示在屏幕顶端

    private View mVolumeBrightnessLayout;//提示窗口
    private TextView mOperationTv;//提示文字
    private ImageView playOrPause;
    private TextView videoCurrentTime;
    private TextView videoTotalTime;
    private SeekBar videoSeekBar;
    private AudioManager mAudioManager;

    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = false;

    public RecordMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private View.OnClickListener fullScreenListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            activity.getDeleteVideo().setVisibility(GONE);
            activity.getDeleteVideo().setEnabled(false);
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            myHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            long newposition = (mDuration * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            if (mInstantSeeking)
                videoView.seekTo(newposition);
            if (videoCurrentTime != null)
                videoCurrentTime.setText(time);
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking)
                videoView.seekTo((mDuration * bar.getProgress()) / 1000);
            show(3000);
            myHandler.removeMessages(SHOW_PROGRESS);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
            mDragging = false;
            myHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    private View.OnClickListener pauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            playOrPause();
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case HIDEFRAM://隐藏提示窗口
                    mVolumeBrightnessLayout.setVisibility(View.GONE);
                    mOperationTv.setVisibility(View.GONE);
                    break;
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

    private long setProgress() {
        if (videoView == null || mDragging)
            return 0;

        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        if (videoSeekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                videoSeekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            videoSeekBar.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (videoTotalTime != null)
            videoTotalTime.setText(StringUtils.generateTime(mDuration));
        if (videoCurrentTime != null)
            videoCurrentTime.setText(StringUtils.generateTime(position));

        return position;
    }

    public RecordMediaController(Context context, VideoView videoView, PlayRecordActivity activity) {
        super(context);
        this.context = context;
        this.videoView = videoView;
        this.activity = activity;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        controllerWidth = wm.getDefaultDisplay().getWidth();
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    protected View makeControllerView() {
        mShowing = true;
        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("media_controller_record", "layout", getContext().getPackageName()), this);
        v.setMinimumHeight(controllerWidth);
        //获取控件
        ImageView fullscreenToggle = (ImageView) v.findViewById(R.id.toggle_fullscreen);
        videoCurrentTime = (TextView) v.findViewById(R.id.video_current_time);
        videoTotalTime = (TextView) v.findViewById(R.id.video_total_time);
        videoSeekBar = (SeekBar) v.findViewById(R.id.video_seekbar);
        playOrPause = (ImageView) v.findViewById(R.id.media_controller_play_pause);
        //声音控制
        mVolumeBrightnessLayout = v.findViewById(R.id.operation_volume_brightness);
        mOperationTv = (TextView) v.findViewById(R.id.operation_tv);
        mOperationTv.setVisibility(View.GONE);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //注册事件监听
        playOrPause.setOnClickListener(pauseListener);
        fullscreenToggle.setOnClickListener(fullScreenListener);
        videoSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        videoSeekBar.setMax(1000);
        myHandler.sendEmptyMessage(SHOW_PROGRESS);
        return v;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
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
        // 隐藏
        myHandler.removeMessages(HIDEFRAM);
        myHandler.sendEmptyMessageDelayed(HIDEFRAM, 1);
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
//            float mOldX = e1.getX(), mOldY = e1.getY();
//            int y = (int) e2.getRawY();
//            int x = (int) e2.getRawX();
//            Display disp = activity.getWindowManager().getDefaultDisplay();
//            int windowWidth = disp.getWidth();
//            int windowHeight = disp.getHeight();
//            if (mOldX > windowWidth * 3.0 / 4.0) {// 右边滑动 屏幕 3/4
//                onVolumeSlide((mOldY - y) / windowHeight);
//            } else if (mOldX < windowWidth * 1.0 / 4.0) {// 左边滑动 屏幕 1/4
//                onBrightnessSlide((mOldY - y) / windowHeight);
//            }
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
     *
     * @param percent
     */
//    private void onVolumeSlide(float percent) {
//        if (mVolume == -1) {
//            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            if (mVolume < 0)
//                mVolume = 0;
//
//            // 显示
//            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
//            mOperationTv.setVisibility(VISIBLE);
//        }
//
//        int index = (int) (percent * mMaxVolume) + mVolume;
//        if (index > mMaxVolume)
//            index = mMaxVolume;
//        else if (index < 0)
//            index = 0;
//        if (index >= 10) {
//            mOperationBg.setImageResource(R.drawable.volumn_3);
//        } else if (index >= 5 && index < 10) {
//            mOperationBg.setImageResource(R.drawable.volumn_2);
//        } else if (index > 0 && index < 5) {
//            mOperationBg.setImageResource(R.drawable.volumn_1);
//        } else {
//            mOperationBg.setImageResource(R.drawable.volumn_0);
//        }
//        //DecimalFormat    df   = new DecimalFormat("######0.00");
//        mOperationTv.setText((int) (((double) index / mMaxVolume) * 100) + "%");
//        // 变更声音
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
//    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
//    private void onBrightnessSlide(float percent) {
//        if (mBrightness < 0) {
//            mBrightness = activity.getWindow().getAttributes().screenBrightness;
//            if (mBrightness <= 0.00f)
//                mBrightness = 0.50f;
//            if (mBrightness < 0.01f)
//                mBrightness = 0.01f;
//
//            // 显示
//            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
//            mOperationTv.setVisibility(VISIBLE);
//
//        }
//
//
//        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
//        lpa.screenBrightness = mBrightness + percent;
//        if (lpa.screenBrightness > 1.0f)
//            lpa.screenBrightness = 1.0f;
//        else if (lpa.screenBrightness < 0.01f)
//            lpa.screenBrightness = 0.01f;
//        activity.getWindow().setAttributes(lpa);
//
//        mOperationTv.setText((int) (lpa.screenBrightness * 100) + "%");
//        if (lpa.screenBrightness * 100 >= 87.5) {
//            mOperationBg.setImageResource(R.drawable.bright_8);
//        } else if (lpa.screenBrightness * 100 >= 75 && lpa.screenBrightness * 100 < 87.5) {
//            mOperationBg.setImageResource(R.drawable.bright_7);
//        } else if (lpa.screenBrightness * 100 >= 62.5 && lpa.screenBrightness * 100 < 75) {
//            mOperationBg.setImageResource(R.drawable.bright_6);
//        } else if (lpa.screenBrightness * 100 >= 50 && lpa.screenBrightness * 100 < 62.5) {
//            mOperationBg.setImageResource(R.drawable.bright_5);
//        } else if (lpa.screenBrightness * 100 >= 37.5 && lpa.screenBrightness * 100 < 50) {
//            mOperationBg.setImageResource(R.drawable.bright_4);
//        } else if (lpa.screenBrightness * 100 >= 25 && lpa.screenBrightness * 100 < 37.5) {
//            mOperationBg.setImageResource(R.drawable.bright_3);
//        } else if (lpa.screenBrightness * 100 >= 12.5 && lpa.screenBrightness * 100 < 25) {
//            mOperationBg.setImageResource(R.drawable.bright_2);
//        } else if (lpa.screenBrightness * 100 >= 0 && lpa.screenBrightness * 100 < 12.5) {
//            mOperationBg.setImageResource(R.drawable.bright_1);
//        }
//
//    }


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
            mShowing = false;
        } else {
            show();
            mShowing = true;
        }
    }

    /**
     * 播放/暂停
     */
    private void playOrPause() {
        if (videoView != null)
            if (videoView.isPlaying()) {
                videoView.pause();
                playOrPause.setImageResource(R.drawable.play_video);
            } else {
                videoView.start();
                playOrPause.setImageResource(R.drawable.stop_video);
            }
    }
}
