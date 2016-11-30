package com.example.susong.testmvp;


/**
 * 全局常量类
 */
public final class C {
    //是否是测试环境
    public static final boolean DEBUG = BuildConfig.DEBUG;
    //是否打印日志
    public static final boolean PRINT_LOG = DEBUG;

    //文件类型
    public enum FileType {
        IMAGE,
        VIDEO,
        AUDIO,
        TEXT
    }
}
