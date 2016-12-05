package com.example.susong.testmvp.http;

import com.example.susong.testmvp.util.IPServFactory;

/**
 * 根域名
 * @author Scott Smith  @Date 2016年06月16/6/12日 14:47
 */
public class HttpRootUrl {
    // 惠民生活
    public static final String ROOT_URL_PPSH = IPServFactory.sharedInstance().currServDomainName();
    // 惠民海淘
    public static String ROOT_URL_PPHT = "http://ht-apponline.ppsh.co";
}
