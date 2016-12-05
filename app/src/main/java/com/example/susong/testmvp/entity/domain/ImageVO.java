package com.example.susong.testmvp.entity.domain;

import java.io.Serializable;

public class ImageVO implements Serializable {
    // 图片路径
    private String path;
    // 图片最近被修改的时间戳
    private long dateTaken;
    // 缩略图
    private String thumbnail;
    // 图片宽度
    private int width;
    // 图片高度
    private int height;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
