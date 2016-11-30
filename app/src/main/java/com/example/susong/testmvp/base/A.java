package com.example.susong.testmvp.base;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.antfortune.freeline.FreelineCore;
import com.example.susong.testmvp.C;
import com.example.susong.testmvp.entity.domain.DeviceInfo;
import com.example.susong.testmvp.util.CrashReporterUtils;
import com.example.susong.testmvp.util.Log.LoggerTool;
import com.example.susong.testmvp.util.UniversualImageLoaderUtils;
import com.example.susong.testmvp.util.exception.IflytekException;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class A extends MultiDexApplication {
    public static A instance;
    private DeviceInfo.Size screenSize;
    private RefWatcher refWatcher;

    private final class InitHandler extends Handler {
        InitHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (C.DEBUG) {
                refWatcher = LeakCanary.install(A.this);
            }
        }
    }

    public A() {
        instance = this;
    }

    public static RefWatcher getRefWatcher(Context context) {
        A application = (A) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FreelineCore.init(this);
        instance = this;
        UniversualImageLoaderUtils.initImageLoader(this);
        initBugly();
        try {
            initSpeech();
        } catch (Exception e) {
            e.printStackTrace();
            CrashReporterUtils.postCatchedException(new IflytekException(e.getMessage() + "<--->科大讯飞初始化失败!!!"));
        }
        initLogger();
//        SsX509TrustManager.allowAllSSL();
//        try {
//            RongCloudUtils.getRongCloudUtils().initRongCloud();
//        } catch (Exception e) {
//            CrashReporterUtils.postCatchedException(new RongCloudException(e.getMessage() + "<--->融云初始化失败!!!"));
//        }
        HandlerThread initThread = new HandlerThread("Init Thread");
        initThread.start();
        InitHandler mInitHandler = new InitHandler(initThread.getLooper());
        mInitHandler.sendEmptyMessage(0);
    }

    /**
     * 初始化腾讯Bugly异常上报库
     */
    private void initBugly() {
        CrashReporterUtils.initCrashReport(this);
    }

    /**
     * 初始化JPush
     */
    private void initJPush() {
//        JPushInterface.requestPermission(this);
//        JPushInterface.setDebugMode(Constants.DEBUG);// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this); // 初始化 JPush
    }

    /**
     * 初始化科大讯飞语音合成
     */
    private void initSpeech() {
//        StringBuffer param = new StringBuffer();
//        param.append("appid=57e0f167");
//        param.append(",");
//        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
//        param.append(",");
//        //此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
//        param.append(SpeechConstant.FORCE_LOGIN + "=true");
//        SpeechUtility.createUtility(this, param.toString());
    }

    private void initLogger() {
        Logger.init("TESTMVP").methodCount(3).logTool(new LoggerTool());
    }

    /**
     * 获取屏幕尺寸大小
     */
    public DeviceInfo.Size getScreentSize() {
        if (screenSize == null) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            screenSize = new DeviceInfo.Size(outMetrics.widthPixels, outMetrics.heightPixels);
        }
        return screenSize;
    }

}
