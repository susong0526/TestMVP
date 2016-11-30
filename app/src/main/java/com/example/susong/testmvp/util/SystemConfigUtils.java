package com.example.susong.testmvp.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.susong.testmvp.BuildConfig;
import com.example.susong.testmvp.base.A;


public class SystemConfigUtils {
    private static SystemConfigUtils systemConfigUtils;
    private static String versionName = "";

    private SystemConfigUtils() {
    }

    public static String getVersionName() {
        if (!TextUtils.isEmpty(versionName)) {
            return versionName;
        }
        try {
            PackageInfo pInfo = A.instance.getPackageManager().getPackageInfo(A.instance.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getVersionStr() {
        StringBuffer sb = new StringBuffer();
        String versionName = getVersionName();
        sb.append(versionName).append(".0.");
        sb.append(getChannel());
        return sb.toString();
    }

    public static String getChannel() {
        return String.valueOf(BuildConfig.channel);
    }

    public static int getVersionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = A.instance.getPackageManager().getPackageInfo(A.instance.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != pInfo) {
            return pInfo.versionCode;
        }
        return 0;
    }

    public static SystemConfigUtils getSystemConfigUtils() {
        if (systemConfigUtils == null) {
            synchronized (SystemConfigUtils.class) {
                systemConfigUtils = new SystemConfigUtils();
            }
        }
        return systemConfigUtils;
    }

    public static <T> T getMetaData(String key, Class<T> c) {
        try {
            ApplicationInfo ai = A
                    .instance
                    .getPackageManager()
                    .getApplicationInfo(A.instance.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            T value = null;
            if (null != bundle) {
                Object object = bundle.get(key);
                if (null != object) {
                    value = (T) object;
                }
            }
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}