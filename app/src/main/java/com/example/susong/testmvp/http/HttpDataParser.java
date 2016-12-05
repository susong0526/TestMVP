package com.example.susong.testmvp.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.example.susong.testmvp.entity.dto.response.ResObj;
import com.example.susong.testmvp.util.CrashReporterUtils;
import com.example.susong.testmvp.util.FastJsonUtils;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.HashMap;


/**
 * 数据解析类,负责根据不同请求解析相应数据
 */
public class HttpDataParser {
    private static HashMap<String, Object[]> url_object_table = new HashMap<>();
    // 基本数据类型
    public static final int TYPE_OBJECT = 1;
    // 列表数据类型
    public static final int TYPE_LIST = 2;

    static {
        RegistConfig.regist(url_object_table);
    }

    public static ResObj parse(String url, String data) {
        ResObj result = null;
        try {
            result = FastJsonUtils.json2Object(data, ResObj.class);
        } catch (Exception e) {
            Logger.e("数据解析异常: url = " + url + ",data = " + data);
            CrashReporterUtils.postCatchedException(new Throwable("数据解析异常: url = " + url + ",data = " + data));
        }
        if (null == result) {
            result = new ResObj();
        }
        return parseData(url, result);
    }

    // 数据解析优化实现类
    private static ResObj parseData(String url, ResObj result) {
        final Object[] values = url_object_table.get(url.split("\\?")[0]);
        if (null != values) {
            final Object data = result.getData();
            String str = null;
            if (null != data) {
                str = JSON.toJSONString(data);
            }
            final Integer type = (Integer) values[0];
            Class<? extends Serializable> cls = (Class<? extends Serializable>) values[1];
            if (null == cls) cls = String.class;
            if (!TextUtils.isEmpty(str)) {
                if (TYPE_OBJECT == type) {
                    result.setData(JSON.parseObject(str, cls));
                } else {
                    result.setData(JSON.parseArray(str, cls));
                }
            }
        }
        return result;
    }
}
