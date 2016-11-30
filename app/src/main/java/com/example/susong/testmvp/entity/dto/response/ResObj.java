package com.example.susong.testmvp.entity.dto.response;

import java.io.Serializable;

/**
 * 服务器返回数据结构
 */
public class ResObj implements Serializable {
    private int code = -1;
    private Object data;
    private Page page;
    private String msg;

    //请求成功
    public static long CODE_SUCCESS = 0;
    //无效Token
    public static long CODE_INVALID_TOKEN = 110002;
    //Token已过期
    public static long CODE_TOKEN_EXPIRED = 110001;
    //资料未完善
    public static long CODE_COMPLETE_INFO = 100027;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
