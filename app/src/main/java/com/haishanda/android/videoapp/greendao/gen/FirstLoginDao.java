package com.haishanda.android.videoapp.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.haishanda.android.videoapp.Bean.FirstLogin;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FIRST_LOGIN".
*/
public class FirstLoginDao extends AbstractDao<FirstLogin, Void> {

    public static final String TABLENAME = "FIRST_LOGIN";

    /**
     * Properties of entity FirstLogin.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property IsFirst = new Property(0, int.class, "isFirst", false, "IS_FIRST");
    }


    public FirstLoginDao(DaoConfig config) {
        super(config);
    }
    
    public FirstLoginDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FIRST_LOGIN\" (" + //
                "\"IS_FIRST\" INTEGER NOT NULL UNIQUE );"); // 0: isFirst
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FIRST_LOGIN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FirstLogin entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getIsFirst());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FirstLogin entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getIsFirst());
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public FirstLogin readEntity(Cursor cursor, int offset) {
        FirstLogin entity = new FirstLogin( //
            cursor.getInt(offset + 0) // isFirst
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FirstLogin entity, int offset) {
        entity.setIsFirst(cursor.getInt(offset + 0));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(FirstLogin entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(FirstLogin entity) {
        return null;
    }

    @Override
    public boolean hasKey(FirstLogin entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}