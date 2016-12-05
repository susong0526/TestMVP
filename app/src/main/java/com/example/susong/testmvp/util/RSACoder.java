package com.example.susong.testmvp.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA安全编码组件
 *
 * @version 1.0
 * @since 1.0
 */
public final class RSACoder {
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key(PKCS8)
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static String RSA_ANDROID = "RSA/ECB/PKCS1Padding";
    private static String RSA_JAVA = "RSA/None/PKCS1Padding";

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param keyBytes    私钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, byte[] keyBytes)
            throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        return processByBlock(encryptedData, cipher, MAX_DECRYPT_BLOCK);
    }

    /**
     * <p>
     * 公钥解密
     * </p>
     * ##android 使用公钥时，必须要注意 使用 "RSA/ECB/PKCS1Padding" 否则必定解密出现乱码
     *      原因在于android 与 java 默认使用的加密方式不一样！！！！（艸）
     * @param encryptedData 已加密数据
     * @param keyBytes     公钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, byte[] keyBytes)
            throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        return processByBlock(encryptedData, cipher, MAX_DECRYPT_BLOCK);
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param keyBytes 公钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] keyBytes)
            throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        return processByBlock(data, cipher, MAX_ENCRYPT_BLOCK);
    }

    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data       源数据
     * @param keyBytes 私钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] keyBytes)
            throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        return processByBlock(data, cipher, MAX_ENCRYPT_BLOCK);
    }

    private static byte[] processByBlock(byte[] data, Cipher cipher, int blockSize) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = data.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > blockSize) {
                cache = cipher.doFinal(data, offSet, blockSize);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * blockSize;
        }
        return out.toByteArray();
    }

//    public static void main(String[] args) throws Exception {
//        String publicStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCveTZ7eupSdnxNBtHAEYLvqSxCK"+
//                "6ys7j6210wXIfv1Dk/Z3WKwue0rg+AoknXTk3dzeNtX5rjeq43Cn101tzD/RIwX"+
//                "KpJKZ5GRh3Nm7Xc0ahdl03gQtd2vxx57Pb2bB/6VQQoPYIM0NLxs39bywjYDeRbz"+"jgNIzggMNosx4FE3RwIDAQAB";
//        String privkeystr="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK95Nnt66lJ2fE0G"+
//                "0cARgu+pLEIrrKzuPrbXTBch+/UOT9ndYrC57SuD4CiSddOTd3N421fmuN6rjcKf"+
//                "XTW3MP9EjBcqkkpnkZGHc2btdzRqF2XTeBC13a/HHns9vZsH/pVBCg9ggzQ0vGzf"+
//                "1vLCNgN5FvOOA0jOCAw2izHgUTdHAgMBAAECgYAAtlM8SRFuaEC051QunWOUqXEe"+
//                "i6LVyNnEUDdk2KR3KCm2zqk54mdOB+j/ASktRKn1dAYf4zXY4YVfxtnQAABY5yvW"+
//                "GpRsSXxNOMz/BQi+L3e0TdKeXWC8CgctrPwHjqo+choliovRff3KSCd4V42ovGpv"+
//                "fChyegZFl/4JAqyLsQJBANaflxyWz0dyMhv8FLAyKeziOZfEf0AVHzPFzrVS8aid"+
//                "Mg63hxo7R875QQlTyr9hB6Dk8F1BehsCyCnnEM3XCN0CQQDRTXBBcfwtB5gaagU3"+
//                "uUfhZ3AOivd6f/uhkrQTSWscNAw/Odpf637dypuH1XIYAPnY/iNXUY65/9va9AGX"+
//                "t2xzAkEAju8N9k5BkTVuRdDl/koKPeaTsI1+qbDnbNVpyryIkGDQO5jicwfT1PWO"+
//                "6KVTO41lRTPzGEhJB8AcijuGp290SQJAKAhOu0XI/Pfh4NN8cf8PP46gQTfVQ+ns"+
//                "wCemJ7Y4NWnDGei/2u7ZEiTpgJ6TtwGgyB87tTzVNsA4VfWrxFAB/wJASUr71jp3"+
//                "I4js9ELYuYwRi7pbpAtflDdFEijD6v/iiykvCyJACr9pe1Sd9+BsWwMmKsoX3n9s"+
//                "WeN+355kv8kApA==";
//        JSONObject o=new JSONObject();
//        o.put("key", "aaa");
//        o.put("key1", "哈哈");
//        o.put("key2", "哇哇");
//        o.put("key3", "呵呵");
//        String data=o.toString();
//        //前端传后端
//        //前端传
//        byte[] date=encryptByPublicKey(data.getBytes(), Base64.decodeBase64(publicStr));
//        String s=Base64.encodeBase64String(date);
//        System.out.println("加密:"+s);
//        //后端解密
//        // System.out.println(new String(decryptByPrivateKey(Base64.decodeBase64(s),Base64.decodeBase64(privkeystr))));
//
//
//        /////////////////////////////////////////////////////////////////////////////////////
//        //后端传前端
//        //后端传
//        s=Base64.encodeBase64String(encryptByPrivateKey(data.getBytes(), Base64.decodeBase64(privkeystr)));
//
//        //前端解密
//        s=new String(decryptByPublicKey(Base64.decodeBase64(s), Base64.decodeBase64(publicStr)));
//        // System.out.println(JSONObject.parse(s));
//
//    }
}
