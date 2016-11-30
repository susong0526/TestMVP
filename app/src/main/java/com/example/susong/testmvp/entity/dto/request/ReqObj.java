package com.example.susong.testmvp.entity.dto.request;

import java.io.Serializable;

/**
 * 请求数据对象,用于封装请求参数
 */
public class ReqObj implements Serializable {
    private String contentType;

    public ReqObj() {
    }

    public ReqObj(String contentType) {
        this.contentType = contentType;
    }

    public boolean needAuth() {
        return true;
    }

    public boolean needExcrypt() {
        return false;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
