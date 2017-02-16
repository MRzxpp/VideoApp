package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.haishanda.android.videoapp.Bean.VideoMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.CustomLandMediaController;
import com.haishanda.android.videoapp.Utils.CustomMediaController;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.Views.MaterialDialog;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * play record
 * Created by Zhongsz on 2016/12/24.
 */

public class PlayRecordActivity extends Activity {
    @BindView(R.id.play_record)
    VideoView playRecordView;

    Bundle extra;
    private String videoPath;
    private String shortPath;

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public long videoDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        videoPath = extra.getString("videoPath");
        shortPath = extra.getString("shortPath");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!playRecordView.isPlaying()) {
            if (videoPath != null) {
                playRecordView.setVideoURI(Uri.fromFile(new File(videoPath)));//设置播放地址
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    CustomMediaController mCustomMediaController = new CustomMediaController(this, playRecordView, this);
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
                playRecordView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.seekTo(0);
                        playRecordView.pause();
                    }
                });
                playRecordView.start();
                setVideoDuration(playRecordView.getDuration());
            }
        }
    }

    @OnClick(R.id.back_to_videos)
    public void backToVideosPage(View view) {
        playRecordView.stopPlayback();
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.delete_video)
    public void deleteVideo(final View view) {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setMessage("是否确认删除?");
        materialDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAction(view);
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void deleteAction(View view) {
        File file = new File(videoPath);
        file.delete();
        VideoMessageDao videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
        QueryBuilder<VideoMessage> queryBuilder = videoMessageDao.queryBuilder();
        VideoMessage videoMessage = queryBuilder.where(VideoMessageDao.Properties.VideoPath.eq(shortPath)).unique();
        videoMessageDao.delete(videoMessage);
        Log.d("PlayRecord", "删除成功");
        backToVideosPage(view);
    }
}
