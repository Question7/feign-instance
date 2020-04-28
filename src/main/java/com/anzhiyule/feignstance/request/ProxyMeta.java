package com.anzhiyule.feignstance.request;

public class ProxyMeta {

    private String host;
    private int port;

    public ProxyMeta() {}

    public ProxyMeta(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static ProxyMeta emptyProxy() {
        return new ProxyMeta();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
