package com.haishanda.android.videoapp.Utils;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.haishanda.android.videoapp.R;

/**
 * Created by Zhongsz on 2016/10/12.
 */

public class NotificationUtil {
    private Activity activity;

    public NotificationUtil(Activity activity) {
        this.activity = activity;
    }

    public NotificationCompat.Builder initNotify(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        builder.setContentTitle("视频监控App")
                .setContentText(message)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher);
        return builder;
    }

    private PendingIntent getDefalutIntent(int flags) {
        return PendingIntent.getActivity(activity, flags, new Intent(), flags);
    }
}
