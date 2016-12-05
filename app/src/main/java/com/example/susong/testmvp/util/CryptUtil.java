package com.example.susong.testmvp.util;

import com.example.susong.testmvp.util.encrypt.Base64;

public class CryptUtil {
    private static final String publicStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCveTZ7eupSdnxNBtHAEYLvqSxCK6ys7j6210wXIfv1Dk/Z3WKwue0rg+AoknXTk3dzeNtX5rjeq43Cn101tzD/RIwX" + "KpJKZ5GRh3Nm7Xc0ahdl03gQtd2vxx57Pb2bB/6VQQoPYIM0NLxs39bywjYDeRbz" + "jgNIzggMNosx4FE3RwIDAQAB";

    public static String encrypt(String obj) throws Exception {
        byte[] date = RSACoder.encryptByPublicKey(obj.getBytes(), Base64.decodeBase64(publicStr));
        return Base64.encodeBase64String(date);
    }
}
