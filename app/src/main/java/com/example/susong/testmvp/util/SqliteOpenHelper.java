package com.example.susong.testmvp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteOpenHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "mvp.db";
    private final static int DATABASE_VERSION = 1;

    public SqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE user(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,pwd TEXT)");
        sqLiteDatabase.execSQL("insert into user(name,pwd) values('admin','1234')");
        sqLiteDatabase.execSQL("CREATE TABLE t_sqlite_cache(id INTEGER PRIMARY KEY AUTOINCREMENT,key varchar not null,source VARCHAR NOT NULL,className VARCHAR NOT NULL,type INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
