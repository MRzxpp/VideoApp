package com.haishanda.android.videoapp.Utils;

import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by Zhongsz on 2016/10/28.
 */

public class SaveImageToLocalUtil {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void saveAction(Bitmap img, String boatName) {
        File sdcardDir = Environment.getExternalStorageDirectory();
        String path = sdcardDir.getPath() + "/VideoApp";
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File boatDir = new File(appDir + "/" + boatName);
        if (!boatDir.exists()) {
            boatDir.mkdir();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日" + "hh时mm分ss秒");
        String imgName = boatName + "_" + format.format(System.currentTimeMillis()) + ".jpg";
        File file = new File(boatDir, imgName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        ImageMessage imageMessage = new ImageMessage(null, boatName, imgName, null, null);
        imageMessageDao.insert(imageMessage);
    }
}
