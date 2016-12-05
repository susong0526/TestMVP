package com.example.susong.testmvp.cache;

import android.text.TextUtils;

import com.example.susong.testmvp.C;
import com.example.susong.testmvp.util.FileUtils;
import com.example.susong.testmvp.util.SpUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 常用文件缓存类
 */
public class NormalFileCache extends FileCache {
    @Override
    public String getAbsolutePath() {
        long userId = SpUtil.getLong(C.KEY_USER_ID);
        if (userId > 0) {
            return C.PATH_FOR_NORMAL_CACHE + File.separator + userId + File.separator + "ppsh.cache";
        }
        return C.PATH_FOR_NORMAL_CACHE + File.separator + "ppsh.cache";
    }

    @Override
    public synchronized void clear() {
        if (null == map) {
            map = new HashMap<>();
        } else {
            map.clear();
        }
        FileUtils.saveSerObjectToFile(map, getAbsolutePath());
    }

    @Override
    public synchronized void clearExclude(String[] keys) {
        if (null == keys || keys.length == 0) return;
        if (null == map || map.size() <= 0) {
            map = (HashMap<String, Object>) FileUtils.readSerObjectFromFile(getAbsolutePath());
        }
        if (null == map || map.size() <= 0) return;
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String removeKey = key;
            for (int i = 0; i < keys.length; i++) {
                if (!TextUtils.isEmpty(keys[i]) && keys[i].equals(key)) {
                    removeKey = null;
                    break;
                }
            }
            if (null != removeKey) {
                iterator.remove();
            }
        }
        FileUtils.saveSerObjectToFile(map, getAbsolutePath());
    }

    @Override
    public void clear(String key) {
        if(null == map) {
            map = (HashMap<String, Object>) FileUtils.readSerObjectFromFile(getAbsolutePath());
        }

        if(null != map) {
            map.remove(key);
        }
        FileUtils.saveSerObjectToFile(map, getAbsolutePath());
    }
}
