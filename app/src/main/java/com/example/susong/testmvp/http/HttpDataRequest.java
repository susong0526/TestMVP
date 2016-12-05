package com.example.susong.testmvp.http;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RedirectError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.susong.testmvp.C;
import com.example.susong.testmvp.entity.dto.request.ReqObj;
import com.example.susong.testmvp.entity.dto.response.ResObj;
import com.example.susong.testmvp.entity.dto.response.ResponseLogObj;
import com.example.susong.testmvp.util.CrashReporterUtils;
import com.example.susong.testmvp.util.CryptUtil;
import com.example.susong.testmvp.util.IPServFactory;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 将数据解析操作放到子线程中去,将数据解析成ResObj对象
 *
 * @author scott
 */
public class HttpDataRequest extends Request<ResObj> {
    private static final String TAG = HttpDataRequest.class.getSimpleName();
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private HttpDataApi.OnRequestCallback onRequestCallback;
    // 短URL
    private String shortUrl;
    // POST请求参数
    private HashMap<String, String> mPostParams;
    // GET请求参数
    private String[] mGetParams;
    // GET请求参数
    private ReqObj mGetReqObj;
    // 请求头
    private Map<String, String> mHeaders;
    private String mContentType = super.getBodyContentType();
    private String mRequestBody;
    private boolean mNeedEncrypt;
    protected static final String PROTOCOL_CHARSET = "utf-8";
    public static final String PROTOCOL_CONTENT_TYPE_JSON = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private String mRootUrl;

    public HttpDataRequest(int method, String shortUrl, HttpDataApi.OnRequestCallback listener) {
        this(method, IPServFactory.sharedInstance().currServDomainName(), shortUrl, listener);
    }

    public HttpDataRequest(int method, String rootUrl, String shortUrl, HttpDataApi.OnRequestCallback listener) {
        super(method, null, null);
        onRequestCallback = listener;
        this.shortUrl = shortUrl;
        mRootUrl = rootUrl;
    }

    @Override
    protected Response<ResObj> parseNetworkResponse(NetworkResponse response) {
        ResObj parsed;
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }
        if (C.DEBUG) {
            ResponseLogObj logObj = new ResponseLogObj();
            logObj.setUrl(getUrl());
            logObj.setData(data);
            try {
                String logStr = JSONObject.toJSONString(logObj);
                Logger.d(logStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parsed = HttpDataParser.parse(shortUrl, data);
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(ResObj response) {
        if (null != onRequestCallback) {
            onRequestCallback.onRequestSuccess(shortUrl, response, false);
        }
    }

    @Override
    public void deliverError(VolleyError volleyError) {
        if (null != onRequestCallback) {
            int statusCode = HttpDataApi.OnRequestCallback.CODE_UNKNOW_ERROR;
            if (volleyError instanceof ServerError) {
                statusCode = volleyError.networkResponse.statusCode;
                String data = new String(volleyError.networkResponse.data);
                try {
                    ResObj resObj = JSONObject.parseObject(data, ResObj.class);
                    onRequestCallback.onRequestError(shortUrl, statusCode, resObj.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashReporterUtils.postCatchedException(e);
                    onRequestCallback.onRequestError(shortUrl, statusCode, null);
                }
            } else {
                // 如果返回了服务器响应错误码,则以响应错误码为准
                if (volleyError instanceof NoConnectionError) {
                    statusCode = HttpDataApi.OnRequestCallback.CODE_NETWORK_NO_CONNECT;
                }
                if (volleyError instanceof NetworkError) {
                    statusCode = HttpDataApi.OnRequestCallback.CODE_NETWORK_ERROR;
                }
                if (volleyError instanceof AuthFailureError) {
                    statusCode = HttpDataApi.OnRequestCallback.CODE_AUTH_FAILURE;
                }
                if (volleyError instanceof TimeoutError) {
                    statusCode = HttpDataApi.OnRequestCallback.CODE_NETWORK_TIME_OUT;
                }
                if (volleyError instanceof RedirectError) {
                    statusCode = HttpDataApi.OnRequestCallback.CODE_REDIRECT_ERROR;
                }
                onRequestCallback.onRequestError(shortUrl, statusCode, volleyError.getMessage());
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }

    @Override
    public String getCacheKey() {
//        long userId = 0;
//        String userData = FileUtils.getStringFromInternalStorage("loginUser");
//        if (!StringUtil.isEmpty(userData)) {
//            LoginUser user = FastJsonUtils.json2Object(userData, LoginUser.class);
//            if (null != user && null != user.getUser()) {
//                userId = user.getUser().getDmId();
//            }
//        }
//        if (userId > 0) {
//            return userId + "/" + getUrl();
//        }
        return super.getCacheKey();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mPostParams;
    }

    void setPostParams(HashMap<String, String> params) {
        mPostParams = params;
    }

    void setGetParams(String... params) {
        mGetParams = params;
    }

    void setGetReqObj(ReqObj reqObj) {
        mGetReqObj = reqObj;
    }

    void addHeaders(String key, String value) {
        if (null == mHeaders) mHeaders = new HashMap<>();
        mHeaders.put(key, value);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (null == mHeaders) {
            mHeaders = new HashMap<>();
        }
        return mHeaders;
    }

    @Override
    public String getUrl() {
        if (getMethod() == Method.POST) {
            return mRootUrl + "/" + shortUrl;
        }
        String tempUrl = shortUrl;
        if (null != mGetReqObj) {
            String jsonString = JSONObject.toJSONString(mGetReqObj);
            HashMap<String, String> params = JSONObject.parseObject(jsonString, HashMap.class);
            Set<String> keySet = params.keySet();
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                if (!tempUrl.contains("?")) {
                    tempUrl += "?";
                }
                String key = it.next();
                String value = params.get(key);
                tempUrl += key + "=" + value + "&";
            }
            if (tempUrl.contains("&")) {
                tempUrl = tempUrl.substring(0, tempUrl.length() - 1);
            }
        } else {
            try {
                if (null != mGetParams && mGetParams.length > 0) {
                    for (String getParam : mGetParams) {
                        tempUrl += "/" + URLEncoder.encode(getParam, "UTF-8");
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Logger.e(e.toString());
            }
        }
        return mRootUrl + "/" + tempUrl;
    }

    @Override
    public String getOriginUrl() {
        return mRootUrl + "/" + shortUrl;
    }

    @Override
    public String getBodyContentType() {
        return mContentType;
    }

    public void setBodyContentType(String contentType) {
        this.mContentType = contentType;
    }

    public void setNeedEncrypt(boolean needEncrypt) {
        this.mNeedEncrypt = needEncrypt;
    }

    public void setBody(String body) {
        if (mNeedEncrypt) {
            try {
                mRequestBody = CryptUtil.encrypt(body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mRequestBody = body;
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            if (!mContentType.equals(super.getBodyContentType())) {
                return mRequestBody == null ? null : mRequestBody.getBytes(DEFAULT_PARAMS_ENCODING);
            }
            return super.getBody();
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, DEFAULT_PARAMS_ENCODING);
            return null;
        }
    }
}
