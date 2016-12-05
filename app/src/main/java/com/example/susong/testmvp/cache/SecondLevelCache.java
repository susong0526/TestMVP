package com.example.susong.testmvp.cache;

/**
 * 二级缓存
 */
public class SecondLevelCache extends Cache {
    private static SecondLevelCache instance;

    private SecondLevelCache() {
    }

    public static SecondLevelCache sharedInstance() {
        if (null == instance) {
            synchronized (SecondLevelCache.class) {
                if (null == instance) {
                    instance = new SecondLevelCache();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void put(String key, Object value) {
        MemoryCache.sharedInstance().put(key, value);
        FileCache.sharedInstance().put(key, value);
    }

    @Override
    public synchronized Object get(String key) {
        Object result = MemoryCache.sharedInstance().get(key);
        if (null == result) {
            result = FileCache.sharedInstance().get(key);
            if (null != result) {
                MemoryCache.sharedInstance().put(key, result);
            }
        }
        return result;
    }

    @Override
    public synchronized void clear() {
        MemoryCache.sharedInstance().clear();
        FileCache.sharedInstance().clear();
    }

    // 排除部分Key值
    public synchronized void clearExclude(String[] keys) {
        MemoryCache.sharedInstance().clearExclude(keys);
        FileCache.sharedInstance().clearExclude(keys);
    }

    @Override
    public void clear(String key) {
        MemoryCache.sharedInstance().clear(key);
        FileCache.sharedInstance().clear(key);
    }
}
