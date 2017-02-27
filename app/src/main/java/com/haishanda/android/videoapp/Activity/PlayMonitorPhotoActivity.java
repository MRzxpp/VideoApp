package com.haishanda.android.videoapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haishanda.android.videoapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 监控报警图片浏览器
 * Created by Zhongsz on 2016/12/12.
 */

public class PlayMonitorPhotoActivity extends Activity {
    Bundle extra;
    @BindView(R.id.monitor_photo_main)
    ViewFlipper photoMain;
    @BindView(R.id.monitor_photos_message)
    TextView monitorPhotoMessage;
    @BindView(R.id.monitor_photo_serial)
    TextView photoSerial;


    private ArrayList<String> imagePathList;
    private String boatName;
    private String monitorTime;
    private GestureDetector gestureDetector;
    private PlayMonitorPhotoActivity instance;
    private int position = 0;

    private final static String TAG = "MonitorPhotoActivity";
    final RequestListener<String, GlideDrawable> photoListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            Log.d(TAG, "Glide load error:" + e.toString());
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_monitor_photo);
        ButterKnife.bind(this);
        synchronized (this) {
            if (instance == null) {
                instance = this;
            }
        }
        extra = getIntent().getExtras();
        imagePathList = extra.getStringArrayList("imagePaths");
        boatName = extra.getString("boatName");
        monitorTime = extra.getString("monitorTime");
    }

    @Override
    protected void onResume() {
        super.onResume();
        photoMain.setAutoStart(false);
        for (int i = 0; i < imagePathList.size(); i++) {
            ImageView imageView = new ImageView(instance);
            Glide
                    .with(this)
                    .load(imagePathList.get(i))
                    .error(R.drawable.monitor_error_big)
                    .listener(photoListener)
                    .into(imageView);
            photoMain.addView(imageView, i);
        }
        photoMain.setDisplayedChild(position);
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
                                   float arg3) {
                if (e2.getX() - e1.getX() > 120) { // 从左向右滑动（左进右出）
                    Animation rInAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_left_in); // 向右滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
                    Animation rOutAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0 -> 0.1）

                    photoMain.setInAnimation(rInAnim);
                    photoMain.setOutAnimation(rOutAnim);
                    photoMain.showPrevious();
                    if (position == 0) {
                        position = imagePathList.size() - 1;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                        return true;
                    }
                    if (position > 0) {
                        position--;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                    }
                    return true;
                } else if (e2.getX() - e1.getX() < -120) { // 从右向左滑动（右进左出）
                    Animation lInAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_right_in); // 向左滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
                    Animation lOutAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_left_out); // 向左滑动右侧滑出的渐变效果（alpha 1.0 -> 0.1）

                    photoMain.setInAnimation(lInAnim);
                    photoMain.setOutAnimation(lOutAnim);
                    photoMain.showNext();
                    if (position == imagePathList.size() - 1) {
                        position = 0;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                        return true;
                    }
                    if (position < imagePathList.size() - 1) {
                        position++;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                    }
                    return true;
                }
                return true;
            }

        });
        photoSerial.setText((position + 1) + "/" + imagePathList.size());
        monitorPhotoMessage.setText(boatName + "  " + monitorTime);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event); // 注册手势事件
    }

    @OnClick(R.id.back_to_photos_btn)
    public void backToLastPage() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.download_monitor_photo)
    public void saveMonitorPhotoToLocal() {
        ImageView view = (ImageView) photoMain.getCurrentView();
        if (!view.getDrawable().equals(getResources().getDrawable(R.drawable.monitor_error_big))) {
            saveMonitorImageToGallery(this, ((BitmapDrawable) view.getDrawable()).getBitmap());
        } else {
            Toast.makeText(this, "图片加载失败，无法保存", Toast.LENGTH_LONG).show();
        }
    }

    public void saveMonitorImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "/VideoApp/" + boatName + "/Monitor");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = monitorTime + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/VideoApp/" + boatName + "/Monitor")));
        Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
    }
}
