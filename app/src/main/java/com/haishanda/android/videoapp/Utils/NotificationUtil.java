package com.haishanda.android.videoapp.Utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.haishanda.android.videoapp.R;

/**
 * 通知栏辅助类
 * 完成初始化通知栏的工作
 * Created by Zhongsz on 2016/10/12.
 */

public class NotificationUtil {
    private Context context;

    public NotificationUtil(Context context) {
        this.context = context;
    }

    public NotificationCompat.Builder initNotify(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("视频监控App")
                .setContentText(message)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher);
        return builder;
    }

    private PendingIntent getDefalutIntent(int flags) {
        return PendingIntent.getActivity(context, flags, new Intent(), flags);
    }
}
