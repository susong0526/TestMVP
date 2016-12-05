package com.example.susong.testmvp;


import com.example.susong.testmvp.util.FileUtils;

/**
 * 全局常量类
 */
public interface C {
    //是否是测试环境
    boolean DEBUG = BuildConfig.DEBUG;
    //是否打印日志
    boolean PRINT_LOG = DEBUG;

    //网络请求成功code
    int CODE_SUCCESS = 0;

    //文件类型
    enum FileType {
        IMAGE,
        VIDEO,
        AUDIO,
        TEXT
    }

    //统一使用它作为xml数据存储
    String SP_NAME = "ppsh";
    //用户Id
    String KEY_USER_ID = "user_id";
    //常用缓存文件路径
    String PATH_FOR_NORMAL_CACHE = FileUtils.getTextCacheDir();
    //图库图片
    String KEY_GALLERY_IMAGES = "gallery_images";
    //User Token
    String KEY_USER_TOKEN = "token";
    //当前测试服务器IP
    String KEY_CURR_SERVER_IP = "curr_server_ip";
}
