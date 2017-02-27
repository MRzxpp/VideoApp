package com.haishanda.android.videoapp.utils.mediacontroller;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.R;

import java.lang.ref.WeakReference;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * 竖屏时的视频控件控制器
 * Created by Zhongsz on 2016/11/2.
 */

public class LiveMediaController extends MediaController {
    private static final int HIDEFRAM = 0;//控制提示窗口的显示

    private GestureDetector mGestureDetector;
    private VideoView videoView;
    private Context context;
    private int controllerWidth = 0;//设置mediaController高度为了使横屏时top显示在屏幕顶端


    private View mVolumeBrightnessLayout;//提示窗口
    private TextView mOperationTv;//提示文字
    private ImageView volumeToggle;
    private ImageView playOrPauseBtn;
    private AudioManager mAudioManager;
    // 当前声音
    private int mVolume = -1;
    private int volumnState = 0;

    public LiveMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

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

    private static class LiveHandler extends Handler {
        private final WeakReference<LiveMediaController> controllerWeakReference;

        LiveHandler(LiveMediaController controller) {
            controllerWeakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            LiveMediaController liveMediaController = controllerWeakReference.get();
            if (liveMediaController != null) {
                switch (msg.what) {
                    case HIDEFRAM://隐藏提示窗口
                        liveMediaController.mVolumeBrightnessLayout.setVisibility(View.GONE);
                        liveMediaController.mOperationTv.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    private final LiveHandler liveHandler = new LiveHandler(this);

    public LiveMediaController(Context context, VideoView videoView) {
        super(context);
        this.context = context;
        this.videoView = videoView;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        controllerWidth = point.x;
    }

    @Override
    protected View makeControllerView() {
        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("media_controller_live", "layout", getContext().getPackageName()), this);
        v.setMinimumHeight(controllerWidth);
        //获取控件
        volumeToggle = (ImageView) v.findViewById(getResources().getIdentifier("toggle_volume", "id", context.getPackageName()));
        playOrPauseBtn = (ImageView) v.findViewById(R.id.media_controller_play_pause);
        //声音控制
        mVolumeBrightnessLayout = v.findViewById(R.id.operation_volume_brightness);
//        ImageView mOperationBg = (ImageView) v.findViewById(R.id.operation_bg);
        mOperationTv = (TextView) v.findViewById(R.id.operation_tv);
        mOperationTv.setVisibility(View.GONE);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        int mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (mVolume == 0) {
            volumeToggle.setImageResource(R.drawable.volumn_off);
        }
        //注册事件监听
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
        volumeToggle.setOnClickListener(volumnListener);
        playOrPauseBtn.setOnClickListener(playOrPauseListener);
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
//        float mBrightness = -1f;
        // 隐藏
        liveHandler.removeMessages(HIDEFRAM);
        liveHandler.sendEmptyMessageDelayed(HIDEFRAM, 1);
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
         *
         * @param e 滑动事件
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
