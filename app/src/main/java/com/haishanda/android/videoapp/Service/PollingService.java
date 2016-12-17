package com.haishanda.android.videoapp.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.haishanda.android.videoapp.Utils.NotificationUtil;

/**
 * Created by Zhongsz on 2016/12/16.
 */

public class PollingService extends Service {

    public static final String ACTION = "com.haishanda.android.videoapp.Service.PollingService";

    private Notification mNotification;
    private NotificationManager mManager;
    private Activity activity;
    private String message;
    private MyBroadCastReceiver myReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        initNotifiManager();
        myReceiver = new MyBroadCastReceiver();
        register();
        new PollingThread().start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
//        new PollingThread().start();
    }

    //初始化通知栏配置
//    private void initNotifiManager() {
//        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        int icon = R.drawable.ic_launcher;
//        mNotification = new Notification();
//        mNotification.icon = icon;
//        mNotification.tickerText = "New Message";
//        mNotification.defaults |= Notification.DEFAULT_SOUND;
//        mNotification.flags = Notific ation.FLAG_AUTO_CANCEL;
//    }

    //弹出Notification
    private void showNotification() {
        int i = (int) (1 + Math.random() * 65535);
        NotificationUtil notificationUtil = new NotificationUtil(activity);
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.notify(i, notificationUtil.initNotify(message).build());
    }

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     *
     * @Author Ryan
     * @Create 2013-7-13 上午10:18:34
     */
    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            System.out.println("Polling...");
            count++;
            //当计数能被30整除时弹出通知
            if (count % 30 == 0) {
                showNotification();
                System.out.println("New message!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String receiveIndex = intent.getStringExtra("message");
            System.out.println(receiveIndex);
        }

    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        //注册广播
        intentFilter.addAction("ACTION_FIRST");
        //第一个参数就是上面声明的MyBroadCastReceive类
        registerReceiver(myReceiver, intentFilter);

    }

}
