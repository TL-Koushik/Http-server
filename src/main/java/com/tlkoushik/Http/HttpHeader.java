package com.tlkoushik.Http;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    public final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }
}
