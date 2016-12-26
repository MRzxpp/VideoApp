package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.CustomLandMediaController;
import com.haishanda.android.videoapp.Utils.CustomMediaController;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Zhongsz on 2016/12/24.
 */

public class PlayRecordActivity extends Activity {
    @BindView(R.id.play_record)
    VideoView playRecordView;

    Bundle extra;
    private String videoPath;
    private CustomMediaController mCustomMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        videoPath = extra.getString("videoPath");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!playRecordView.isPlaying()) {
            if (videoPath != null) {
                playRecordView.setVideoURI(Uri.fromFile(new File(videoPath)));//设置播放地址
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mCustomMediaController = new CustomMediaController(this, playRecordView, this);
                    mCustomMediaController.show(5000);
                    playRecordView.setMediaController(mCustomMediaController);//绑定控制器
                } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    CustomLandMediaController landMediaController = new CustomLandMediaController(this, playRecordView, this);
                    landMediaController.show(5000);//控制器显示5s后自动隐藏
                    playRecordView.setMediaController(landMediaController);
                }
                playRecordView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);//设定缩放参数
                playRecordView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
                playRecordView.requestFocus();//取得焦点
                playRecordView.start();
            }
        } else {
            playRecordView.start();
        }
    }

    @OnClick(R.id.back_to_videos)
    public void backToVideosPage() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
