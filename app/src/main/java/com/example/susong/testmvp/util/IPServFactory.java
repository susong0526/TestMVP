package com.example.susong.testmvp.util;

import android.text.TextUtils;

import com.example.susong.testmvp.C;
import com.example.susong.testmvp.cache.SecondLevelCache;

/**
 * 用于产生服务器IP
 */
public class IPServFactory {
    private static IPServFactory instance;

    public static IPServFactory sharedInstance() {
        if (null == instance) {
            instance = new IPServFactory();
        }
        return instance;
    }

    /**
     * 外部生产环境地址
     * @return
     */
    public String externalProductEnv() {
        return "app.yjzp.net.cn";
    }


    /**
     * 内部生产环境地址
     * @return
     */
    public String internalProductEnv() {
        return "test1.hmsh.com";
    }

    public String getScheme() {
        return "http://";
    }

    /**
     * 获取当前IP
     * @return
     */
    public String currIPServ() {
        if (C.DEBUG) {
            String ipSrv = (String) SecondLevelCache.sharedInstance().get(C.KEY_CURR_SERVER_IP);
            if (!TextUtils.isEmpty(ipSrv)) return ipSrv;
        }
        return externalProductEnv();
    }

    /**
     * 切换到外部生产环境
     * @return
     */
    public String switchToExternalProductEnv() {
        SecondLevelCache.sharedInstance().put(C.KEY_CURR_SERVER_IP, externalProductEnv());
        return externalProductEnv();
    }

    /**
     * 切换到内部生产环境
     * @return
     */
    public String switchToInternalProductEnv() {
        SecondLevelCache.sharedInstance().put(C.KEY_CURR_SERVER_IP, internalProductEnv());
        return internalProductEnv();
    }

    /**
     * 获取当前服务器域名
     * @return
     */
    public String currServDomainName() {
        if (C.DEBUG) {
            String ipSrv = (String) SecondLevelCache.sharedInstance().get(C.KEY_CURR_SERVER_IP);
            if (!TextUtils.isEmpty(ipSrv)) {
                return getScheme() + ipSrv;
            }
        }
        return getScheme() + externalProductEnv();
    }
}
