package com.example.susong.testmvp.http;

import com.example.susong.testmvp.entity.dto.request.ReqObj;
import com.example.susong.testmvp.entity.dto.response.ResObj;

/**
 * 用于处理网络请求
 *
 * @author scott
 */
public abstract class HttpDataApi {

    /**
     * HTTP请求回调接口
     *
     * @author scott
     */
    public interface OnRequestCallback {
        /**
         * 网络未连接
         */
        int CODE_NETWORK_NO_CONNECT = -1;
        /**
         * 网络请求超时
         */
        int CODE_NETWORK_TIME_OUT = -2;
        /**
         * 服务器异常
         */
        int CODE_SERVER_ERROR = -3;
        /**
         * 网络错误
         */
        int CODE_NETWORK_ERROR = -4;
        /**
         * 授权失败
         */
        int CODE_AUTH_FAILURE = -5;
        /**
         * 重定向错误
         */
        int CODE_REDIRECT_ERROR = -6;
        /**
         * 未知错误
         */
        int CODE_UNKNOW_ERROR = -7;

        /**
         * 数据请求成功回调,statusCode == 200
         *
         * @param url        请求URL
         * @param result     请求结果
         * @param isFrmCache 是否来自缓存数据
         */
        void onRequestSuccess(String url, ResObj result, boolean isFrmCache);

        /**
         * 数据请求失败回调,statusCode != 200
         *
         * @param url        请求URL
         * @param statusCode 请求失败状态码 (404,500...)
         * @param error      请求错误提示信息
         */
        void onRequestError(String url, int statusCode, String error);
    }

    public abstract void GET(String shortUrl, boolean needAuth, String... params);

    public abstract void POST(String shortUrl, ReqObj reqObj);

}
