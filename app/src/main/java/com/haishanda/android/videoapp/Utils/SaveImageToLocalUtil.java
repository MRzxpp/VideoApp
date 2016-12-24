package com.haishanda.android.videoapp.Utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import com.haishanda.android.videoapp.Bean.BoatMessage;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.Bean.VideoMessage;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;
import com.haishanda.android.videoapp.greendao.gen.VideoMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Zhongsz on 2016/10/28.
 */

public class SaveImageToLocalUtil {

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        File dateDir = new File(boatDir + "/" + dateFormat.format(System.currentTimeMillis()));
        if (!dateDir.exists()) {
            dateDir.mkdir();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy年MM月dd日" + "hh时mm分ss秒");
        String imgName = boatName + "_" + timeFormat.format(System.currentTimeMillis()) + ".jpg";
        File file = new File(dateDir, imgName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        ImageMessage imageMessage = new ImageMessage(null, boatName, imgName, dateFormat.format(System.currentTimeMillis()), null);
        imageMessageDao.insert(imageMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void saveCameraIconAction(Bitmap img, String boatName, long cameraId) {
        File sdcardDir = Environment.getExternalStorageDirectory();
        String path = sdcardDir.getPath() + "/VideoApp";
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File boatDir = new File(appDir + "/" + boatName + "/CameraIcons");
        if (!boatDir.exists()) {
            boatDir.mkdir();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日" + "hh时mm分ss秒");
        String imgName = format.format(System.currentTimeMillis()) + ".jpg";
        File file = new File(boatDir, imgName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            BoatMessageDao boatMessageDao = VideoApplication.getApplication().getDaoSession().getBoatMessageDao();
            BoatMessage boatMessage;
            QueryBuilder<BoatMessage> builder = boatMessageDao.queryBuilder();
            boatMessage = builder.where(BoatMessageDao.Properties.CameraId.eq(cameraId)).unique();
            boatMessage.setCameraImagePath(boatDir + "/" + imgName);
            boatMessageDao.insertOrReplace(boatMessage);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void saveVideoIconAction(Bitmap img, String boatName, String time) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        File dateDir = new File(boatDir + "/" + dateFormat.format(System.currentTimeMillis()));
        if (!dateDir.exists()) {
            dateDir.mkdir();
        }
        File iconDir = new File(dateDir + "/Videos");
        if (!iconDir.exists()) {
            iconDir.mkdir();
        }
        String imgName = "videoicon_" + boatName + "_" + time + ".jpg";
        File file = new File(iconDir, imgName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        VideoMessageDao videoMessageDao = VideoApplication.getApplication().getDaoSession().getVideoMessageDao();
        QueryBuilder<VideoMessage> queryBuilder = videoMessageDao.queryBuilder();
        VideoMessage videoMessage = queryBuilder.where(VideoMessageDao.Properties.AddTime.eq(time)).uniqueOrThrow();
        videoMessage.setIconPath(iconDir + "/" + imgName);
        videoMessageDao.update(videoMessage);
    }
}
