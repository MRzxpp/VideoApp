package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Zhongsz on 2016/10/19.
 */

public class PlayVideoActivity extends Activity {
    @BindView(R.id.test_play_video)
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
//        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))return;
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
}
