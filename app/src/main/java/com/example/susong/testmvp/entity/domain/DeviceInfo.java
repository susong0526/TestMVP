package com.example.susong.testmvp.entity.domain;

import java.io.Serializable;

/**
 * 用户设备信息收集类
 */
public class DeviceInfo implements Serializable {
    //设备唯一标示
    private String IMEI;
    //网络连接类型 WIFI / MOBILE
    private String networkType;
    //手机的MAC地址
    private String MAC;
    //手机的IP地址
    private String IPAddress;
    //设备名称
    private String deviceName;
    //应用版本
    private String version;
    //设备系统版本号
    private String OSVersion;

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOSVersion() {
        return OSVersion;
    }

    public void setOSVersion(String OSVersion) {
        this.OSVersion = OSVersion;
    }

    public static class Size implements Serializable {
        private int width;
        private int height;

        public Size(int width, int height) {

            this.width = width;
            this.height = height;
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
}
