package com.haishanda.android.videoapp.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.haishanda.android.videoapp.bean.VideoMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.mediacontroller.RecordLandMediaController;
import com.haishanda.android.videoapp.utils.mediacontroller.RecordMediaController;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.views.MaterialDialog;
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
    @BindView(R.id.delete_video)
    ImageView deleteVideo;
    @BindView(R.id.back_to_videos)
    ImageView backToVideosBtn;

    public ImageView getDeleteVideo() {
        return deleteVideo;
    }

    Bundle extra;
    private String videoPath;
    private String shortPath;
    private final static String TAG = "PlayRecordActivity";

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "orientation changed");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            deleteVideo.setVisibility(View.GONE);
            deleteVideo.setEnabled(false);
            backToVideosBtn.setVisibility(View.GONE);
            backToVideosBtn.setEnabled(false);
            //横屏
            RecordLandMediaController landMediaController = new RecordLandMediaController(this, playRecordView, this);
            //控制器显示5s后自动隐藏
            landMediaController.show(5000);
            playRecordView.setMediaController(landMediaController);
            //设置全屏即隐藏状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            playRecordView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            deleteVideo.setVisibility(View.VISIBLE);
            deleteVideo.setEnabled(true);
            backToVideosBtn.setVisibility(View.VISIBLE);
            backToVideosBtn.setEnabled(true);
            RecordMediaController recordMediaController = new RecordMediaController(this, playRecordView, this);
            recordMediaController.show(5000);
            playRecordView.setMediaController(recordMediaController);
            //恢复状态栏
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            playRecordView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!playRecordView.isPlaying()) {
            if (videoPath != null) {
                playRecordView.setVideoURI(Uri.fromFile(new File(videoPath)));//设置播放地址
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    RecordMediaController mRecordMediaController = new RecordMediaController(this, playRecordView, this);
                    mRecordMediaController.setMediaPlayer(playRecordView);
                    mRecordMediaController.show(5000);
                    playRecordView.setMediaController(mRecordMediaController);//绑定控制器
                    playRecordView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
                } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    playRecordView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
                    deleteVideo.setEnabled(false);
                    deleteVideo.setVisibility(View.GONE);
                    backToVideosBtn.setVisibility(View.GONE);
                    backToVideosBtn.setEnabled(false);
                    //设置全屏即隐藏状态栏
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    RecordLandMediaController landMediaController = new RecordLandMediaController(this, playRecordView, this);
                    landMediaController.show(5000);//控制器显示5s后自动隐藏
                    playRecordView.setMediaController(landMediaController);
                }
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
            }
        }
    }

    @OnClick(R.id.back_to_videos)
    public void backToVideosPage() {
        playRecordView.stopPlayback();
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.delete_video)
    public void deleteVideo() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setMessage("是否确认删除?");
        materialDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAction();
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

    private void deleteAction() {
        File file = new File(videoPath);
        file.delete();
        VideoMessageDao videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
        QueryBuilder<VideoMessage> queryBuilder = videoMessageDao.queryBuilder();
        VideoMessage videoMessage = queryBuilder.where(VideoMessageDao.Properties.VideoPath.eq(shortPath)).unique();
        videoMessageDao.delete(videoMessage);
        Log.d("PlayRecord", "删除成功");
        backToVideosPage();
    }
}
