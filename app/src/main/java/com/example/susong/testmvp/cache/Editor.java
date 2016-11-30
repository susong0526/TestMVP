package com.example.susong.testmvp.cache;

/**
 * 用于数据批量提交
 */
public interface Editor {
    void put(String key, Object value);

    void commit();

    void setCache(PPSHCache cache);
}
