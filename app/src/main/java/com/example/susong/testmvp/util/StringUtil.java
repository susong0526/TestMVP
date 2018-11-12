package com.example.susong.testmvp.util;

import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final boolean isEmpty(String msg) {
        return msg == null || msg.trim().length() == 0;
    }

    public static final boolean isEmpty(CharSequence msg) {
        return msg == null || msg.toString().trim().length() == 0;
    }

    public static String hideCreditNumber(String creditNumber) {
        if (creditNumber != null && creditNumber.length() > 4) {
            String showPart = creditNumber.substring(creditNumber.length() - 4, creditNumber.length());
            return "**** **** **** " + showPart;
        }
        return "";
    }

    /**
     * 是否是有效的电话号码
     *
     * @param phoneNumber 电话号码
     */
    public static final boolean isValidatePhoneNumber(String phoneNumber) {
        if (StringUtil.isEmpty(phoneNumber) || phoneNumber.length() != 11 || !phoneNumber.substring(0, 1).equals("1")) {
            return false;
        }
        return true;
//        String reg = "(^(13\\d|14[57]|15[^4,\\D]|17[023678]|18\\d)\\d{8}|170[059]\\d{7})$";
//        return Pattern.matches(reg, phoneNumber);
    }

    /**
     * 判断是否为固定电话
     *
     * @param phoneNumber
     * @return
     */
    public static final boolean isValidateFixedLineNumber(String phoneNumber) {
        String reg = "1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";
        return Pattern.matches(reg, phoneNumber);
    }

    /**
     * 是否有效的昵称
     */
    public static final boolean isValidateNickname(String nickname) {
        return !(StringUtil.isEmpty(nickname) || nickname.length() < 2 || nickname.length() > 12);
    }

    public static final boolean isValidateAccount(String account) {
        String reg = "^[A-Za-z0-9]{6,16}+$";
        return Pattern.matches(reg, account);
    }

    /**
     * 获取唯一字符串
     */
    public static String getUniqueString() {
        return UUID.randomUUID().toString().replace("-", "") + Math.abs(new Random().nextInt(10000));
    }

    /**
     * 判断字符串是否有2-4个字符组成
     */
    public static boolean isChineseName(String chineseStr) {
        String rex = "[\\u4e00-\\u9fa5]{2,100}";
        return Pattern.matches(rex, chineseStr);
    }

    /**
     * 判断是否是有效的身份证号码
     */
    public static boolean isValidateIDNumber(String idNumber) {
        String rex = "((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65)[0-9]{4})(([1|2][0-9]{3}[0|1][0-9][0-3][0-9][0-9]{3}[Xx0-9])|([0-9]{2}[0|1][0-9][0-3][0-9][0-9]{3}))";
        return Pattern.matches(rex, idNumber);
    }

    /**
     * 毫秒转换为日期
     *
     * @param milliseconds 毫秒
     * @return
     */
    public static String milliseconds2Date(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date(milliseconds));
        return date;
    }

    /**
     * 判断是否是有效的营业执照号码
     */
    public static boolean isValidateLicenceNum(String licenceNum) {
        String rex = "[\\\\d]{15}";
        return Pattern.matches(rex, licenceNum);
    }

    /**
     * 验证是否为有效的密码（长度为6-16个字符，不能包含空格，不能是9位以下纯数字）
     * 适用于登录时只非空验证
     *
     * @param password
     */
    public static final boolean isValidatePasswordByLogin(String password) {

        //        String regTemp = "^(?!\\d{1,8}$)[\\S]{6,16}$";

        //        return Pattern.matches(regTemp, password);

        return !StringUtil.isEmpty(password);
    }

    /**
     * 验证是否为有效的密码（长度为6-16个字符，不能包含空格，不能是9位以下纯数字）
     *
     * @param password
     */
    public static final boolean isValidatePassword(String password) {
        String regTemp = "^(?!\\d{1,8}$)[\\S]{6,16}$";
        return Pattern.matches(regTemp, password);
    }

    /**
     * 是否为有效的验证码（不少于6位的数字）
     *
     * @param verifyCode
     * @return
     */
    public static final boolean isValidateVerifyCode(String verifyCode) {
        String rex = "[0-9]{6,}";
        return Pattern.matches(rex, verifyCode);
    }

    /**
     * 认证的姓名是否有效(认证姓名不能包含特殊字符)
     *
     * @param authName 认证姓名
     * @return true有效的姓名 false无效的姓名
     */
    public static final boolean containsSpecialCharacter(String authName) {
        if (StringUtil.isEmpty(authName)) {
            return true;
        }
//        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\d]";
        String regEx = "([\\u4E00-\\u9FA5]{2,10})";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(authName);
        return m.find();
    }

    /**
     * 是否为有效的邮箱
     * @param email
     * @return
     */
    public static final boolean isValidateEmail(String email) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 判断是否是有效的Http地址
     */
    public static boolean isValidateHttpUrl(String url) {
        Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    /**
     * 计算字符串长度
     *
     * @param authName 认证姓名
     * @return 认证姓名的长度
     */
    public static final int calculateAuthNameLength(String authName) {
        if (StringUtil.isEmpty(authName)) {
            return 0;
        }
        int length = 0;
        char arr[] = authName.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            if ((c >= 0x0391 && c <= 0xFFE5)) { //英文字符
                length = length + 2;
            } else if ((c >= 0x0000 && c <= 0x00FF)) { //中文字符
                length = length + 1;
            }
        }
        return length;
    }

    /**
     * 获取操作时间
     *
     * @param time
     * @return
     */
    public static final String getOperaTime(long time) {
        return getOperaTime(System.currentTimeMillis(), time);
    }

    public static final String getOperaTime(long serverTime, long operaTime) {
        if (operaTime != 0) {
            long duration = serverTime - operaTime;
            if (duration < 0) {
                return "1分前";
            }
            int hour = (int) (duration / (1000 * 60 * 60));
            int minute = (int) (duration % (1000 * 60 * 60) / (1000 * 60));
            if (hour == 0) {
                return minute + "分前";
            } else if (hour < 24) {
                return hour + "时前";
            } else if (hour > 24) {
                return hour / 24 + "天前";
            }
        }
        return "1分前";
    }

    /**
     * 米转换为KM
     */
    public static String m2KM(double m) {
        double km = m / 1000f;
        if (km < 1) {
            if (m == 0) {
                m = 1;
            }
            return (int) m + "m";
        } else {
            return (int) km + "km";
        }
        //        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        //        return decimalFormat.format(km) + "km";
    }

    /**
     * 米转换为KM
     */
    public static String m2KMForCar(double m) {
        double km = m / 1000f;
        if (km < 1) {
            if (m == 0) {
                m = 1;
            }
            return (int) m + "米";
        } else {
            return (int) km + "公里";
        }
        //        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        //        return decimalFormat.format(km) + "km";
    }

    /**
     * 使用字符串生成一个utf8字节数组
     *
     * @param pArray 字符串
     * @return utf8字节数组或null
     */
    public static byte[] getBytesUtf8(String pArray) {

        byte[] strByte = null;
        try {
            strByte = pArray.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strByte;
    }

    /**
     * 使用字节数组生成一个utf8字符串
     *
     * @param strByte 字节数组
     * @return utf8字符串或null
     */
    public static String newStringUtf8(byte[] strByte) {
        String result = null;
        try {
            result = new String(strByte, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入字符串
     *
     * @param content
     * @param file
     */
    public static void writeString(String content, File file) {
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * SHA256加密算法
     *
     * @param text
     * @return
     */
    public static String encodeToSHA256(String text) {
        return sha256(text);
    }

    public static String sha256(String plainText) {
        if(TextUtils.isEmpty(plainText)) return "";
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainText.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte hex : hash) {
                String hexStr = Integer.toHexString(0xff & hex);
                if(hexStr.length() == 1) hexString.append('0');
                hexString.append(hexStr);
            }
            return hexString.toString();
        } catch (Throwable e) {
            e.printStackTrace();
            CrashReporterUtils.postCatchedException(e);
        }
        return plainText;
//        ByteArrayInputStream is = new ByteArrayInputStream(plainText.getBytes());
//        String output;
//        int read;
//        byte[] buffer = new byte[8192];
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            while ((read = is.read(buffer)) > 0) {
//                digest.update(buffer, 0, read);
//            }
//            byte[] hash = digest.digest();
//            BigInteger bigInt = new BigInteger(1, hash);
//            output = bigInt.toString(16);
//            while (output.length() < 32) {
//                output = "0" + output;
//            }
//        } catch (Exception e) {
//            return null;
//        }
//        return output;
    }

    /**
     * 处理字符串，null转成空字符串
     */
    public static String getString(String value) {
        return isEmpty(value) ? "" : value;
    }

    /**
     * 过滤空字符串，空字符串显示默认字符串
     */
    public static String filterString(String value, String defaultValue) {
        return !isEmpty(value) ? value : defaultValue;
    }

    /**
     * 格式化字符,保留小数位 %[argument_index$][flags][width][.precision]conversion
     * @param amount
     * @param fractionDigits
     * @return
     */
    public static String formatAmount(double amount, int fractionDigits) {
        return String.format(Locale.CHINA, "%."+ fractionDigits +"f", amount);
    }

    public static String formatAmount(String amountStr, int fractionDigits) {
        if (!TextUtils.isEmpty(amountStr)) {
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                return formatAmount(amount, fractionDigits);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return amountStr;
        }
        return "";
    }

    public static boolean isValidServIP(String ip) {
        final String regex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static boolean isValidServPort(int port) {
        return port > 0;
    }

    public static boolean isValidDomainName(String domainName) {
        final String regex = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(domainName);
        return matcher.matches();
    }

    /**
     * 验证是否是吉粮惠民回调协议
     * @param text
     * @return
     */
    public static boolean isPPSHProtocol(String text, String key) {
        String regex = "^(?i)jlsy://.*" + key + ".*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * 验证是否是商品列表回调协议
     * @param text
     * @return
     */
    public static boolean isGoodProtocol(String text, String key) {
        String regex = "^(?i)hmclient://.*" + key + ".*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * 从字符串解析参数
     * @param str
     * @return
     */
    public static TreeMap<String, String> parseFromStr(String str) {
        TreeMap<String, String> resMap = new TreeMap<>();
        if (null == str || "".equals(str)) {
            return resMap;
        }
        try {
            str = URLDecoder.decode(str,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return resMap;
        }
        if (str.contains("?")) {
            String firstKey = str.substring(0, str.indexOf("?"));
            if (!TextUtils.isEmpty(firstKey)) {
                resMap.put("firstkey", firstKey);
            }
        } else {
            String firstKey = str.substring(0, str.length());
            if (!TextUtils.isEmpty(firstKey)) {
                resMap.put("firstkey", firstKey);
            }
        }
        String second = str.substring(str.indexOf("?") + 1, str.length());
        String[] reArray = second.split("&");
        for (String tmp : reArray) {
            String[] tmpArr = tmp.split("=");
            if(tmpArr.length == 2){
                resMap.put(tmpArr[0], tmpArr[1]);
            }else {
                resMap.put(tmpArr[0], "");
            }
        }
        return resMap;
    }

}
