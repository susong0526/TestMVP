package com.example.susong.testmvp.cache;

import java.util.HashMap;

/**
 * 用于文件数据批量提交
 */
public class FileEditor implements Editor {
    private FileCache mCache;
    private HashMap<String, Object> map = new HashMap<>();

    @Override
    public void put(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public void commit() {
        mCache.putToMemory(map);
        mCache.commit();
    }

    @Override
    public void setCache(PPSHCache cache) {
        mCache = (FileCache) cache;
    }
}
