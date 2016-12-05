package com.example.susong.testmvp.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

/**
 * FastJson操作Json数据工具类
 */
public class FastJsonUtils {
    public static <T extends Object> T json2Object(String jsonString) {
        try {
            return json2Object(jsonString, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T extends Object> T json2Object(String jsonString, Class<T> c) {
        if (TextUtils.isEmpty(jsonString)) return null;
        try {
            T t;
            if (null != c) {
                t = JSON.parseObject(jsonString, c);
            } else {
                t = (T) JSON.parse(jsonString);
            }
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    public static String object2Json(Object o) {
        if (o == null) {
            return "";
        }
        return JSON.toJSONString(o);
    }
}
