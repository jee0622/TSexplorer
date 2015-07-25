package com.tscale.tsexplorer.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rd-19 on 2015/7/20.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "COUNT.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "count";
    private final static String COUNT_ID = "count_id";
    private final static String ADDRESS = "address";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COUNT_ID
                + " INTEGER primary key autoincrement, " + ADDRESS + " text, " + USERNAME + " text, " + PASSWORD + " text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }


    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " ;", new String[]{});
        return cursor;
    }

    public Cursor selectByAddress(String address) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where address = ? ;", new String[]{address});
        return cursor;
    }


    public long insert(String address, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put(ADDRESS, address);
        cv.put(USERNAME, username);
        cv.put(PASSWORD, password);
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    public void updateById(String id, String address, String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        String where = COUNT_ID + " = ?";
        String[] whereValue = {id};
        cv.put(ADDRESS, address);
        cv.put(USERNAME, username);
        cv.put(PASSWORD, password);
        db.update(TABLE_NAME, cv, where, whereValue);
    }
}
