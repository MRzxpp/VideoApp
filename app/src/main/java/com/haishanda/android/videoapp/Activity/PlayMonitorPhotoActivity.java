package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/12/12.
 */

public class PlayMonitorPhotoActivity extends Activity {
    Bundle extra;
    @BindView(R.id.photo_main)
    ImageView photoMain;
    @BindView(R.id.monitor_photos_message)
    TextView monitorPhotoMessage;


    private String imagePath;
    private String boatName;
    private String monitorTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_monitor_photo);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        imagePath = extra.getString("imagePath");
        boatName = extra.getString("boatName");
        monitorTime = extra.getString("monitorTime");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this)
                .load(imagePath)
                .error(R.drawable.monitor_error_big)
                .into(photoMain);
        monitorPhotoMessage.setText(boatName + "  " + monitorTime);
    }

    @OnClick(R.id.back_to_photos_btn)
    public void backToLastPage() {
        this.finish();
    }

    @OnClick(R.id.download_monitor_photo)
    public void saveMonitorPhotoToLocal() {
        saveMonitorImageToGallery(this, ((BitmapDrawable) photoMain.getDrawable()).getBitmap());
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
