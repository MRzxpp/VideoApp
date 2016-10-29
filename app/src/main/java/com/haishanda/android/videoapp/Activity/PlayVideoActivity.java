package com.haishanda.android.videoapp.Activity;

import android.app.Activity;

import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.SaveImageToLocalUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;

import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        Vitamio.isInitialized(getApplicationContext());
        ButterKnife.bind(this);

        String path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
        videoView.setVideoPath(path);//设置播放地址
        MediaController mMediaController = new MediaController(this);
        mMediaController.show(5000);//控制器显示5s后自动隐藏
        videoView.setMediaController(mMediaController);//绑定控制器
        videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
        videoView.requestFocus();//取得焦点
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.printscreen_btn)
    public void printScreen(View view) {
        Log.i(TAG, "printScreen");
        SaveImageToLocalUtil.saveAction(videoView.getCurrentFrame(), "aaa");
        List<ImageMessage> list = imageMessageDao.loadAll();
    }

    @OnClick(R.id.record_btn)
    public void recordVideo(View view) {
        Log.i(TAG, "recordVideo");
    }
}
