package com.tlkoushik.Http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class HttpMessage {
    private Map<String, String> headers;
    private byte[] messageBody;

    HttpMessage() {
        headers = new HashMap<>();
        messageBody = new byte[0];
    }

    public Set<String> getHeaders() {
        return this.headers.keySet();
    }

    public String getHeaderValue(String header) {
        return headers.getOrDefault(header.toLowerCase(), "None");
    }

    public void setMessage(byte[] body) {
        messageBody = body;
    }

    public byte[] getMessageBody() {
        return messageBody;
    }

    public void addHeader(String header, String value) {
        headers.put(header.toLowerCase(), value);
    }

}
