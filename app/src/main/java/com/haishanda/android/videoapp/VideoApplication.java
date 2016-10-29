package com.haishanda.android.videoapp;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.haishanda.android.videoapp.greendao.gen.DaoMaster;
import com.haishanda.android.videoapp.greendao.gen.DaoSession;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

/**
 * Created by Zhongsz on 2016/10/12.
 */

public class VideoApplication extends Application {
    private static VideoApplication application;
    public static Context instance;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    // login user name
//    public final String PREF_USERNAME = "username";

    /**
     * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
     */
//    public static String currentUserNick = "";
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        setDatabase();
    }

    public static Context getInstance() {
        return instance;
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
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public SQLiteDatabase getDb() {
        return db;
    }
}
