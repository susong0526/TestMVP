package com.example.susong.testmvp.cache;

import com.example.susong.testmvp.util.FileUtils;

import java.util.HashMap;

/**
 * 文件缓存
 */
public abstract class FileCache extends Cache {
    private static FileCache instance;
    protected HashMap<String, Object> map;
    private FileEditor mEditor;

    public static FileCache sharedInstance() {
        if (null == instance) {
            synchronized (FileCache.class) {
                if (null == instance) {
                    instance = new NormalFileCache();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void put(String key, Object value) {
        prepare();
        if (null != value) {
            map.put(key, value);
        }
        if (null != getAbsolutePath() && null != value) {
            FileUtils.saveSerObjectToFile(map, getAbsolutePath());
        }
    }

    @Override
    public synchronized Object get(String key) {
        if (null == map) {
            map = (HashMap<String, Object>) FileUtils.readSerObjectFromFile(getAbsolutePath());
        }
        if (null != map) {
            return map.get(key);
        }
        return null;
    }

    private void prepare() {
        if (null == map) {
            map = (HashMap<String, Object>) FileUtils.readSerObjectFromFile(getAbsolutePath());
        }
        if (null == map) {
            map = new HashMap<>();
        }
    }

    public synchronized void putToMemory(HashMap<String, Object> data) {
        prepare();
        if (null != data && data.size() > 0) {
            map.putAll(data);
        }
    }

    public void commit() {
        if (null != getAbsolutePath() && null != map) {
            FileUtils.saveSerObjectToFile(map, getAbsolutePath());
        }
        mEditor = null;
    }

    public Editor edit() {
        if (null == mEditor) {
            mEditor = new FileEditor();
            mEditor.setCache(this);
        }
        return mEditor;
    }

    public abstract String getAbsolutePath();

}
