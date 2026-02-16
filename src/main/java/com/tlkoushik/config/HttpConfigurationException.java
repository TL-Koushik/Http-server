package com.tlkoushik.config;

public class HttpConfigurationException extends RuntimeException {
    public HttpConfigurationException() {

    }

    public HttpConfigurationException(String msg) {
        super(msg);
    }

    public HttpConfigurationException(Throwable cause) {
        super(cause);
    }

    public HttpConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
