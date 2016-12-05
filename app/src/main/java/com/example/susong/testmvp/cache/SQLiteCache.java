package com.example.susong.testmvp.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.example.susong.testmvp.base.A;
import com.example.susong.testmvp.util.SqliteOpenHelper;

import java.util.List;

/**
 * 使用SQLite数据库作为缓存工具
 */
public class SQLiteCache extends Cache {
    private static SQLiteCache instance;
    private SQLiteOpenHelper mSQLiteOpenHelper;

    // 数据类型
    // 基本对象
    private final int TYPE_OBJECT = 0;
    // List集合类型
    private final int TYPE_LIST = 1;
    // 类类型
    private final String CLASS_TYPE_UNKNOWN = "unknown";
    // LRU Cache
    private LruCache<String, Object> mMemeroyCache;

    public static SQLiteCache sharedInstance() {
        if (null == instance) {
            synchronized (MemoryCache.class) {
                if (null == instance) {
                    instance = new SQLiteCache();
                }
            }
        }
        return instance;
    }

    @Override
    public void put(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) return;
        prepare();
        String source = JSON.toJSONString(value);
        String className;
        int type;
        // 集合数据类型
        if (value instanceof List) {
            List list = (List) value;
            if (list.isEmpty()) {
                className = CLASS_TYPE_UNKNOWN;
            } else {
                Object object = list.get(0);
                className = object.getClass().getName();
            }
            type = TYPE_LIST;
        } else {
            // 对象数据类型
            className = value.getClass().getName();
            type = TYPE_OBJECT;
        }
        SQLiteDatabase database = mSQLiteOpenHelper.getWritableDatabase();
        // 首先查询指定Key是否存在,如存在则直接更新即可
        Cursor cursor = database.rawQuery("select count(id) c from t_sqlite_cache where key = ?", new String[]{key});
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(cursor.getColumnIndex("c"));
        }
        cursor.close();
        database.beginTransaction();
        try {
            if (count <= 0) {
                ContentValues values = new ContentValues();
                values.put("key", key);
                values.put("source", source);
                values.put("className", className);
                values.put("type", type);
                database.insert("t_sqlite_cache", null, values);
            } else {
                ContentValues values = new ContentValues();
                values.put("key", key);
                values.put("source", source);
                values.put("className", className);
                values.put("type", type);
                database.update("t_sqlite_cache", values, "key = ?", new String[]{key});
            }
            database.setTransactionSuccessful();
            mMemeroyCache.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        database.close();
    }

    @Override
    public Object get(String key) {
        if (TextUtils.isEmpty(key)) return null;
        prepare();
        if (null != mMemeroyCache.get(key)) return mMemeroyCache.get(key);
        SQLiteDatabase database = mSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("t_sqlite_cache", null, "key = ?", new String[]{key}, null, null, null);
        if (cursor.moveToNext()) {
            String source = cursor.getString(cursor.getColumnIndex("source"));
            String className = cursor.getString(cursor.getColumnIndex("className"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            // List类型
            try {
                Object result;
                if (TYPE_LIST == type) {
                    if (CLASS_TYPE_UNKNOWN.equals(className)) {
                        result = JSON.parseArray(source);
                    } else {
                        result = JSON.parseArray(source, Class.forName(className));
                    }
                } else {
                    result = JSON.parseObject(source, Class.forName(className));
                }
                mMemeroyCache.put(key, result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void clear() {
    }

    private void prepare() {
        if (null == mSQLiteOpenHelper) {
            mSQLiteOpenHelper = new SqliteOpenHelper(A.instance);
        }
        if (null == mMemeroyCache) {
            //获取系统分配给每个应用程序的最大内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int mCacheSize = maxMemory / 8;
            mMemeroyCache = new LruCache<>(mCacheSize);
        }
    }

    @Override
    public void clearExclude(String[] keys) {

    }

    @Override
    public void clear(String key) {

    }
}
