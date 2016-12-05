package com.example.susong.testmvp.entity.domain.eventbus;

/**
 * 监听网络状态变化
 */
public class NetworkChangeEvent extends EventObj {
    public static final int UNKNOWN = -1;
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    public static final int SLOW = 3;
    public static final int UNSTABLE = 4;

    private String network;
    private int status = UNKNOWN;

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
