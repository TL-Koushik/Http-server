package com.tlkoushik.config;

public class Configuration {
    private String webroot;
    private int port;

    public String getWebroot() {
        return this.webroot;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setWebroot(String webroot) {
        this.webroot = webroot;
    }
}
