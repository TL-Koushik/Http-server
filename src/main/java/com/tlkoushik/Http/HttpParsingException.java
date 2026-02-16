package com.tlkoushik.Http;

public class HttpParsingException extends Exception {

    HttpStatusCode errorCode;

    public HttpParsingException() {
    }

    public HttpParsingException(HttpStatusCode errorCode) {
        super(errorCode.MESSAGE);
    }

    public HttpStatusCode getStatusCode() {
        return this.errorCode;
    }
}
