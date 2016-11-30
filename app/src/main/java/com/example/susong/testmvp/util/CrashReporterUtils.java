package com.example.susong.testmvp.util;

import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.example.susong.testmvp.C;
import com.example.susong.testmvp.base.A;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Map;

/**
 * Bugly程序崩溃上报类
 */
public class CrashReporterUtils {

    /**
     * 初始化Bugly异常上报
     * @param application
     */
    public static void initCrashReport(android.app.Application application) {
        if (application == null) {
            return;
        }
        CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(application.getApplicationContext());
        userStrategy.setAppChannel(String.valueOf(SystemConfigUtils.getChannel()));
        userStrategy.setAppVersion(SystemConfigUtils.getVersionName());
        userStrategy.setDeviceID(SystemInfoUtils.getDeviceInfo().getIMEI());
        userStrategy.setAppReportDelay(5000);
        /** 自动初始化开关: true表示app启动自动初始化升级模块; false不会自动初始化; 开发者如果担心sdk初始化影响app启动速度，可以设置为false，在后面某个时刻手动调用Beta.init(getApplicationContext(),false);*/
        Beta.autoInit = true;
        /** 自动检查更新开关: true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;*/
        Beta.autoCheckUpgrade = false;
        /** 延迟初始化: 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度; */
        Beta.initDelay = 3 * 1000;
        /** 设置sd卡的Download为更新资源存储目录 */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        /** 设置开启显示打断策略 */
        Beta.showInterruptedStrategy = true;
//        Beta.upgradeCheckPeriod = 60 * 1000;
        if(C.DEBUG) {
            Bugly.init(application, "13bb3aad4c", C.DEBUG, userStrategy);
        } else {
            Bugly.init(application, "44867ad292", C.DEBUG, userStrategy);
        }
        // 增加用户Id上报
//        LoginUser user = FragmentBase.getLoginUser();
//        if(null != user && null != user.getUser()) {
//            CrashReport.setUserId(user.getUser().getMphonenum() + "");
//        }
    }

    /**
     * 上报捕捉到的异常到Bugly
     */
    public static void postCatchedException(Throwable throwable) {
        if (throwable == null) {
            return;
        }
        CrashReport.postCatchedException(throwable);
    }

    /**
     * 上报捕捉到的异常到Bugly
     */
    public static void postCatchedException(Class c, String methodName, String exceptionDetail) {
        if (c == null || TextUtils.isEmpty(methodName) || TextUtils.isEmpty(exceptionDetail)) {
            return;
        }
        String connectivityTypeDescription = SystemInfoUtils.getConnectivityTypeDescription(A.instance);
        NetworkInfo.State connectivityStatus = SystemInfoUtils.getConnectivityStatus(A.instance);
        String message = "异常所在类: " + c.getName()
                + ";异常所在方法: "
                + methodName
                + ";异常详细描述: "
                + exceptionDetail
                + ";网络类型: "
                + connectivityTypeDescription
                + " 网络连接状态: "
                + (connectivityStatus == null ? "" : connectivityStatus.name());
        postCatchedException(new Throwable(message));
    }

    /**
     * 上报网络连接异常
     */
    public static void postNetworkError(String url, Map<String, String> params, VolleyError volleyError) {
        if (volleyError == null) {
            return;
        }
//        LoginUser loginUser = FragmentBase.getLoginUser();
//        long userID = 0;
//        String userName = null;
//        if (loginUser != null && loginUser.getUser() != null) {
//            userID = loginUser.getUser().getDmId();
//            CrashReport.setUserId(userID + "");
//            userName = loginUser.getUser().getNickname();
//        }
//        StringBuffer httpErrorMsg = new StringBuffer();
//        httpErrorMsg.append("userID = " + userID + "; userName = " + userName + "; ");
//        String errorType = volleyError.getClass().getName();
//        httpErrorMsg.append("请求地址：" + url + "; ");
//        String paramsStr = JSON.toJSONString(params);
//        httpErrorMsg.append("请求参数： " + paramsStr + "; ");
//        httpErrorMsg.append("错误类型： " + errorType + "; ");
//        NetworkResponse networkResponse = volleyError.networkResponse;
//        if (networkResponse == null) {
//            httpErrorMsg.append("volleyError.networkResponse == null,未知错误");
//        } else {
//            int statusCode = networkResponse.statusCode;
//            byte[] data = networkResponse.data;
//            String errorDescription = new String(data);
//            httpErrorMsg.append("状态码： " + statusCode + "; ");
//            httpErrorMsg.append("错误描述： " + errorDescription + " .");
//        }
//        CrashReporterUtils.postCatchedException(HttpUtils.class, "makingRequest（）", httpErrorMsg.toString());
    }

}
