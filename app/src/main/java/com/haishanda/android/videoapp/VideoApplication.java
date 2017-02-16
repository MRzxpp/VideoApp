package com.haishanda.android.videoapp;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.haishanda.android.videoapp.greendao.gen.DaoMaster;
import com.haishanda.android.videoapp.greendao.gen.DaoSession;
import com.pgyersdk.crash.PgyCrashManager;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

/**
 * 应用类,存放部分全局变量
 * Created by Zhongsz on 2016/10/12.
 */

public class VideoApplication extends Application {
    private static VideoApplication application;
    private SQLiteDatabase db;
    private DaoSession mDaoSession;
    private int currentMachineId;

    public int getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
    }

    private int selectedId;

    public int getCurrentMachineId() {
        return currentMachineId;
    }

    public void setCurrentMachineId(int currentMachineId) {
        this.currentMachineId = currentMachineId;
    }

    public String getCurrentBoatName() {
        return currentBoatName;
    }

    public void setCurrentBoatName(String currentBoatName) {
        this.currentBoatName = currentBoatName;
    }

    private String currentBoatName;


    /**
     * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
     */
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        setDatabase();
        PgyCrashManager.register(this);
        ZXingLibrary.initDisplayOpinion(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static VideoApplication getApplication() {
        return application;
    }

    /**
     * 设置greenDao
     */

    private void setDatabase() {

        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "video_db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public SQLiteDatabase getDb() {
        return db;
    }
}
