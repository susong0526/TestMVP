package com.example.susong.testmvp.cache;

/**
 * 缓存
 */
public abstract class PPSHCache {
    /**
     * 保存数据
     *
     * @param key
     * @param value
     */
    public abstract void put(String key, Object value);

    /**
     * 获取数据
     *
     * @param key
     * @return
     */
    public abstract Object get(String key);

    /**
     * 清除缓存数据
     */
    public abstract void clear();

    /**
     * 保留指定的key值
     *
     * @param keys
     */
    public abstract void clearExclude(String[] keys);

    /**
     * 清除指定的Key值
     * @param key
     */
    public abstract void clear(String key);
}
