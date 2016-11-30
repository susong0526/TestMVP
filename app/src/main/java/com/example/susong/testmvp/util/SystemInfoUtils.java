package com.example.susong.testmvp.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.example.susong.testmvp.base.A;
import com.example.susong.testmvp.entity.domain.DeviceInfo;
import com.orhanobut.logger.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class SystemInfoUtils {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 4;
    public static final int TYPE_UNKNOWN = 3;

    /**
     * 获取手机IMEI
     */
    public static DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        ConnectivityManager connectivityManager = (ConnectivityManager) A.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            int type = activeNetworkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                deviceInfo.setNetworkType("wifi");
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                deviceInfo.setNetworkType("mobile");
            }
        }
        deviceInfo.setOSVersion(Build.VERSION.RELEASE);
        deviceInfo.setDeviceName(Build.BRAND + "-" + Build.PRODUCT);
        WifiManager wifi = (WifiManager) A.instance.getSystemService(Context.WIFI_SERVICE);
        if (null != wifi) {
            WifiInfo info = wifi.getConnectionInfo();
            if (null != info) {
                String macAddress = info.getMacAddress();
                int ipAddress = info.getIpAddress();
                deviceInfo.setMAC(macAddress);
                deviceInfo.setIPAddress(ipAddress + "");
            }
        }
        if (TextUtils.isEmpty(deviceInfo.getIPAddress())) {
            deviceInfo.setIPAddress(getIpAddress());
        }
        int permissionCheck = ContextCompat.checkSelfPermission(A.instance, "android.permission.READ_PHONE_STATE");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) A.instance.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();
            deviceInfo.setIMEI(deviceId);
        }
        return deviceInfo;
    }

    /**
     * 网络是否可用
     */
    public static boolean isNetworkAvaliable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) A.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }
        return false;
    }

    /**
     * 获取网络连接状态
     */
    public static int getConnectivityType(Context context) {
        if (context == null) {
            Logger.d("未知网络，传入的参数为空");
            return TYPE_UNKNOWN;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Logger.d("连接到Wifi");
                return TYPE_WIFI;
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Logger.d("连接到蜂窝网络");
                return TYPE_MOBILE;
            }
        }
        Logger.d("网络未连接");
        return TYPE_NOT_CONNECTED;
    }

    /**
     * 获取当前网络的连接状态
     */
    public static NetworkInfo.State getConnectivityStatus(Context context) {
        if (context == null) {
            return NetworkInfo.State.UNKNOWN;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    Logger.d("有网络连接 : " + activeNetwork.getState().name());
                    return activeNetwork.getState();
                }
            }
        }
        Logger.d("无网络连接");
        return NetworkInfo.State.DISCONNECTED;
    }

    /**
     * 获取网络连接状态
     *
     * @param context
     */
    public static String getConnectivityTypeDescription(Context context) {
        if (context == null) {
            return null;
        }
        int conn = getConnectivityType(context);
        String status = "未知网络";
        if (conn == TYPE_WIFI) {
            status = "Wifi";
        } else if (conn == TYPE_MOBILE) {
            status = "蜂窝网络";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "未连接到网络";
        }
        return status;
    }

    /**
     * 获取当前进程名称
     */
    public static String getCurProcessName(Context context) {
        if (context == null) {
            return null;
        }
        int myPid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses != null && runningAppProcesses.size() > 0) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                    if (processInfo.pid == myPid) {
                        return processInfo.processName;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 3G网络IP
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断指定应用是否安装
     *
     * @param pkgName
     * @return
     */
    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断服务是否运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
