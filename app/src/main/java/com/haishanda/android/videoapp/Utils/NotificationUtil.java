package com.haishanda.android.videoapp.Utils;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;

import com.haishanda.android.videoapp.R;

/**
 * Created by Zhongsz on 2016/10/12.
 */

public class NotificationUtil {
    private NotificationCompat.Builder builder;
    private Activity activity;

    public NotificationUtil(Activity activity) {
        this.activity = activity;
    }

    public NotificationCompat.Builder initNotify() {
        builder = new NotificationCompat.Builder(activity);
        builder.setContentTitle("视频监控App")
                .setContentText("测试通知栏")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setTicker("测试")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher);
        return builder;
    }

    private PendingIntent getDefalutIntent(int flags) {
        return PendingIntent.getActivity(activity, 1, new Intent(), flags);
    }
}
