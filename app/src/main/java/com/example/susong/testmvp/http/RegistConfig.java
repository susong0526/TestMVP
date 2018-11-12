package com.example.susong.testmvp.http;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegistConfig {
    public static void regist(HashMap<String, Object[]> url_object_table) {
//        register(HttpDataReqUrl.URL_GET_PURSE_BANKCARD, HttpDataParser.TYPE_OBJECT, ResWalletBankCardObj.class, url_object_table);
//        register(HttpDataReqUrl.URL_CALCULATE_WITHDRAW_MONTY, HttpDataParser.TYPE_OBJECT, ResCalcWdMonObj.class, url_object_table);
    }

    private static void register(@NonNull String url, int type, @Nullable Class<? extends Serializable> cls, HashMap<String, Object[]> url_object_table) {
        if (TextUtils.isEmpty(url)) {
            throw new RuntimeException("url不能为空");
        }
        if (HttpDataParser.TYPE_OBJECT != type && HttpDataParser.TYPE_LIST != type) {
            throw new RuntimeException("type取值只能为: #TYPE_OBJECT 或 #TYPE_LIST");
        }
        Object[] value = new Object[]{type, cls};
        url_object_table.put(url, value);
    }
}
