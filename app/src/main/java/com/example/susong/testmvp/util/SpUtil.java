package com.example.susong.testmvp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.susong.testmvp.C;
import com.example.susong.testmvp.base.A;

/**
 * SharePreferences 辅助类
 */
public class SpUtil {
    public static void putBoolean(String spName, String key, boolean value) {
        SharedPreferences sp = A.instance.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String spName, String key, boolean defaultValue) {
        SharedPreferences sp = A.instance.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void putString(String spName, String key, String value) {
        SharedPreferences sp = A.instance.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String spName, String key) {
        SharedPreferences sp = A.instance.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void putLong(String spName, String key, long value) {
        SharedPreferences sp = A.instance.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String spName, String key) {
        SharedPreferences sp = A.instance.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

    public static void putString(String key, String value) {
        putString(C.SP_NAME, key, value);
    }

    public static String getString(String key) {
        return getString(C.SP_NAME, key);
    }

    public static void putLong(String key, long value) {
        putLong(C.SP_NAME, key, value);
    }

    public static long getLong(String key) {
        return getLong(C.SP_NAME, key);
    }
}
