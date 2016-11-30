package com.example.susong.testmvp.cache;

import android.text.TextUtils;
import android.util.LruCache;

/**
 * 内存缓存
 */
public class MemoryCache extends PPSHCache {
    private static MemoryCache instance;
    private LruCache<String, Object> mMemoryCache;

    public static MemoryCache sharedInstance() {
        if (null == instance) {
            synchronized (MemoryCache.class) {
                if (null == instance) {
                    instance = new MemoryCache();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void put(String key, Object value) {
        if (null == mMemoryCache) {
            //获取系统分配给每个应用程序的最大内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int mCacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<>(mCacheSize);
        }
        if (null != value && key != null) {
            mMemoryCache.put(key, value);
        }
    }

    @Override
    public synchronized Object get(String key) {
        if (!TextUtils.isEmpty(key) && null != mMemoryCache) return mMemoryCache.get(key);
        return null;
    }

    @Override
    public synchronized void clear() {
        if (null != mMemoryCache) {
            mMemoryCache.evictAll();
        }
    }

    // 由于LRUCache自身实现的原因,该方法暂无实现方式,只能采用clear()方法代替
    public synchronized void clearExclude(String[] keys) {
        if(null == keys || keys.length == 0 || null == mMemoryCache) return;
        clear();
    }

    @Override
    public void clear(String key) {
        if(!TextUtils.isEmpty(key)) {
            if(null != mMemoryCache) {
                mMemoryCache.remove(key);
            }
        }
    }
}
