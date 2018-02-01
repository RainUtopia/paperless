package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/1/24.
 */

public class IpInfo {
    String ip;//ip
    int port;//端口

    public IpInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
