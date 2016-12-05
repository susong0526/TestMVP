package com.example.susong.testmvp.http;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.susong.testmvp.C;
import com.example.susong.testmvp.base.A;
import com.example.susong.testmvp.base.activity.ActivityBaseCompat;
import com.example.susong.testmvp.base.activity.delegate.ActivityDelegate;
import com.example.susong.testmvp.base.fragment.FragmentBaseCompat;
import com.example.susong.testmvp.base.fragment.delegate.FragmentDelegate;
import com.example.susong.testmvp.entity.dto.request.ReqObj;
import com.example.susong.testmvp.util.FileUtils;
import com.example.susong.testmvp.util.SystemConfigUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据请求类
 */
public class HttpDataLoader extends HttpDataApi implements ActivityDelegate, FragmentDelegate {
    private static RequestQueue mRequestQueue;
    private OnRequestCallback onRequestCallback;
    // 当前正在处理的Request
    private ArrayList<Request> mCurrentRequests;

    public HttpDataLoader(OnRequestCallback requestCallback) {
        onRequestCallback = requestCallback;
        if (null == mRequestQueue) {
            mRequestQueue = Volley.newRequestQueue(A.instance.getApplicationContext());
        }
        mCurrentRequests = new ArrayList<>();
        // 仅仅支持新版ActivityBaseCompat 和 新版FragmentBaseCompat
        if (requestCallback instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) onRequestCallback).addDelegate(this);
        }
        if (requestCallback instanceof FragmentBaseCompat) {
            ((FragmentBaseCompat) onRequestCallback).addDelegate(this);
        }
    }

    /**
     * 发起GET请求
     *
     * @param shortUrl 短链接
     * @param needAuth 是否需要认证,即携带Token参数
     * @param params GET请求参数,需要按顺序传入 (Restful风格API)
     */
    @Override
    public void GET(@NonNull final String shortUrl, boolean needAuth, String... params) {
        HttpDataRequest request = new HttpDataRequest(Request.Method.GET, shortUrl, onRequestCallback);
        request.setGetParams(params);
        request.setShouldCache(true);
        // 设置请求重试策略
        int timeoutMs = 30000;
        if (C.DEBUG) {
            timeoutMs = 20000;
        }
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutMs, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 如果需要携带Token信息,needAuth需要设置为true
        if (needAuth) {
            String userToken = FileUtils.getStringFromInternalStorage("tokenID");
            if (!TextUtils.isEmpty(userToken)) {
                request.addHeaders(C.KEY_USER_TOKEN, userToken);
            }
        }
        // 传入平台版本等相关数据
        // 平台
        request.addHeaders("platform", "Android");
        // 版本名称
        request.addHeaders("versionName", SystemConfigUtils.getVersionName() + "");
        // 版本号
        request.addHeaders("versionCode", SystemConfigUtils.getVersionCode() + "");
        // 系统版本
        request.addHeaders("sdkVersion", Build.VERSION.SDK_INT + "");
        mRequestQueue.add(request);
        mCurrentRequests.add(request);
    }


    /**
     * 发起GET请求
     * 使用?形式拼接GET请求参数请使用该方法
     *
     * @param shortUrl 短链接
     */
    public void GET(@NonNull final String shortUrl, @NonNull ReqObj reqObj) {
        HttpDataRequest request = new HttpDataRequest(Request.Method.GET, shortUrl, onRequestCallback);
        request.setShouldCache(true);
        request.setGetReqObj(reqObj);
        // 设置请求重试策略
        int timeoutMs = 30000;
        if (C.DEBUG) {
            timeoutMs = 20000;
        }
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutMs, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 如果需要携带Token信息,needAuth需要设置为true
        if (reqObj.needAuth()) {
            String userToken = FileUtils.getStringFromInternalStorage("tokenID");
            if (!TextUtils.isEmpty(userToken)) {
                request.addHeaders(C.KEY_USER_TOKEN, userToken);
            }
        }
        // 传入平台版本等相关数据
        // 平台
        request.addHeaders("platform", "Android");
        // 版本名称
        request.addHeaders("versionName", SystemConfigUtils.getVersionName() + "");
        // 版本号
        request.addHeaders("versionCode", SystemConfigUtils.getVersionCode() + "");
        // 系统版本
        request.addHeaders("sdkVersion", Build.VERSION.SDK_INT + "");
        mRequestQueue.add(request);
        mCurrentRequests.add(request);
    }

    /**
     * 发起GET请求
     *
     * @param rootUrl 根域名
     * @param shortUrl 短链接
     * @param needAuth 是否需要认证,即携带Token参数
     * @param params GET请求参数,需要按顺序传入 (Restful风格API)
     */
    public void GET(@NonNull final String rootUrl, @NonNull final String shortUrl, boolean needAuth, String... params) {
        HttpDataRequest request = new HttpDataRequest(Request.Method.GET, rootUrl, shortUrl, onRequestCallback);
        request.setGetParams(params);
        request.setShouldCache(true);
        // 设置请求重试策略
        int timeoutMs = 30000;
        if (C.DEBUG) {
            timeoutMs = 20000;
        }
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutMs, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 如果需要携带Token信息,needAuth需要设置为true
        if (needAuth) {
            String userToken = FileUtils.getStringFromInternalStorage("tokenID");
            if (!TextUtils.isEmpty(userToken)) {
                request.addHeaders(C.KEY_USER_TOKEN, userToken);
            }
        }
        // 传入平台版本等相关数据
        // 平台
        request.addHeaders("platform", "Android");
        // 版本名称
        request.addHeaders("versionName", SystemConfigUtils.getVersionName() + "");
        // 版本号
        request.addHeaders("versionCode", SystemConfigUtils.getVersionCode() + "");
        // 系统版本
        request.addHeaders("sdkVersion", Build.VERSION.SDK_INT + "");
        mRequestQueue.add(request);
        mCurrentRequests.add(request);
    }

    /**
     * 发起GET请求
     * 使用?形式拼接GET请求参数请使用该方法
     *
     * @param shortUrl 短链接
     */
    public void GET(@NonNull String rootUrl, @NonNull final String shortUrl, @NonNull ReqObj reqObj) {
        HttpDataRequest request = new HttpDataRequest(Request.Method.GET, rootUrl, shortUrl, onRequestCallback);
        request.setShouldCache(true);
        request.setGetReqObj(reqObj);
        // 设置请求重试策略
        int timeoutMs = 30000;
        if (C.DEBUG) {
            timeoutMs = 20000;
        }
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutMs, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 如果需要携带Token信息,needAuth需要设置为true
        if (reqObj.needAuth()) {
            String userToken = FileUtils.getStringFromInternalStorage("tokenID");
            if (!TextUtils.isEmpty(userToken)) {
                request.addHeaders(C.KEY_USER_TOKEN, userToken);
            }
        }
        // 传入平台版本等相关数据
        // 平台
        request.addHeaders("platform", "Android");
        // 版本名称
        request.addHeaders("versionName", SystemConfigUtils.getVersionName() + "");
        // 版本号
        request.addHeaders("versionCode", SystemConfigUtils.getVersionCode() + "");
        // 系统版本
        request.addHeaders("sdkVersion", Build.VERSION.SDK_INT + "");
        mRequestQueue.add(request);
        mCurrentRequests.add(request);
    }

    /**
     * 发起POST请求
     *
     * @param shortUrl 短链接
     * @param reqObj POST请求参数
     */
    @Override
    public void POST(@NonNull final String shortUrl, @NonNull ReqObj reqObj) {
        final HttpDataRequest request = new HttpDataRequest(Request.Method.POST, shortUrl, onRequestCallback);
        request.setShouldCache(true);
        // 设置请求重试策略
        int timeoutMs = 30000;
        if (C.DEBUG) {
            timeoutMs = 20000;
        }
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutMs, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 如果需要携带Token信息,needAuth需要设置为true
        if (reqObj.needAuth()) {
            String userToken = FileUtils.getStringFromInternalStorage("tokenID");
            if (!TextUtils.isEmpty(userToken)) {
                request.addHeaders(C.KEY_USER_TOKEN, userToken);
            }
        }
        request.setNeedEncrypt(reqObj.needExcrypt());
        if (!TextUtils.isEmpty(reqObj.getContentType())) {
            request.setBodyContentType(reqObj.getContentType());
            request.setBody(JSONObject.toJSONString(reqObj));
        } else {
            String jsonString = JSON.toJSONString(reqObj);
            request.setPostParams(JSON.parseObject(jsonString, HashMap.class));
        }
        // 传入平台版本等相关数据
        // 平台
        request.addHeaders("platform", "Android");
        // 版本名称
        request.addHeaders("versionName", SystemConfigUtils.getVersionName() + "");
        // 版本号
        request.addHeaders("versionCode", SystemConfigUtils.getVersionCode() + "");
        // 系统版本
        request.addHeaders("sdkVersion", Build.VERSION.SDK_INT + "");
        mRequestQueue.add(request);
        mCurrentRequests.add(request);
    }

    /**
     * 发起POST请求
     *
     * @param shortUrl 短链接
     * @param reqObj POST请求参数
     */
    public void POST(@NonNull String rootUrl, @NonNull final String shortUrl, @NonNull ReqObj reqObj) {
        final HttpDataRequest request = new HttpDataRequest(Request.Method.POST, rootUrl, shortUrl, onRequestCallback);
        request.setShouldCache(true);
        // 设置请求重试策略
        request.setRetryPolicy(new DefaultRetryPolicy());
        // 如果需要携带Token信息,needAuth需要设置为true
        if (reqObj.needAuth()) {
            String userToken = FileUtils.getStringFromInternalStorage("tokenID");
            if (!TextUtils.isEmpty(userToken)) {
                request.addHeaders(C.KEY_USER_TOKEN, userToken);
            }
        }
        if (!TextUtils.isEmpty(reqObj.getContentType())) {
            request.setBodyContentType(reqObj.getContentType());
            request.setBody(JSONObject.toJSONString(reqObj));
        } else {
            String jsonString = JSON.toJSONString(reqObj);
            request.setPostParams(JSON.parseObject(jsonString, HashMap.class));
        }
        // 传入平台版本等相关数据
        // 平台
        request.addHeaders("platform", "Android");
        // 版本名称
        request.addHeaders("versionName", SystemConfigUtils.getVersionName() + "");
        // 版本号
        request.addHeaders("versionCode", SystemConfigUtils.getVersionCode() + "");
        // 系统版本
        request.addHeaders("sdkVersion", Build.VERSION.SDK_INT + "");
        mRequestQueue.add(request);
        mCurrentRequests.add(request);
    }

    // 结束调用后调用该方法移除事件监听,并标记为cancel
    public void finish() {
        onRequestCallback = null;
        if (null != mCurrentRequests) {
            for (Request request : mCurrentRequests) {
                request.cancel();
            }
            mCurrentRequests.clear();
            mCurrentRequests = null;
        }
    }

    @Override
    public void onAttach(Context context) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroyView() {
        finish();
    }

    @Override
    public void onFragmentDestroy() {
        finish();
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onActivityDestroy() {
        finish();
    }
}
