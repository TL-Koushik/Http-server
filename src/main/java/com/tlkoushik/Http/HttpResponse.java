package com.tlkoushik.Http;

import java.nio.charset.StandardCharsets;

public class HttpResponse extends HttpMessage {

    // the first line of the respose is called status_line
    // below is the status_line stucture
    // status_line = HTTP-version SP status-code SP reason-phrase CRLF

    private final static String CRLF = "\r\n";

    private HttpVersion httpVersion;

    private HttpStatusCode statusCode;

    private String responsePhase = null;

    private HttpResponse() {

    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponsePhase() {
        if (responsePhase == null && statusCode != null) {
            return statusCode.MESSAGE;
        }
        return responsePhase;
    }

    public void setResponsePhase(String responsePhase) {
        this.responsePhase = responsePhase;
    }

    // HTTP-version SP status-code SP reason-phrase CRLF

    public byte[] getResponseBytes() {
        StringBuilder responseString = new StringBuilder();

        responseString.append(this.httpVersion.LITERAL).append(" ")
                .append(this.statusCode.STATUS_CODE).append(" ")
                .append(getResponsePhase())
                .append(CRLF);

        for (String header : getHeaders()) {
            responseString.append(header)
                    .append(": ")
                    .append(getHeaderValue(header))
                    .append(CRLF);
        }

        responseString.append(CRLF);

        byte[] responseBytes = responseString.toString().getBytes(StandardCharsets.US_ASCII);

        if (getMessageBody().length == 0)
            return responseBytes;

        byte[] responseWithBody = new byte[responseBytes.length + getMessageBody().length];

        System.arraycopy(responseBytes, 0, responseWithBody, 0, responseBytes.length);
        System.arraycopy(getMessageBody(), 0, responseWithBody, responseBytes.length, getMessageBody().length);

        return responseWithBody;
    }

    public static class Builder {
        private HttpResponse response = new HttpResponse();

        public Builder statusCode(HttpStatusCode statusCode) {
            response.statusCode = statusCode;
            return this;
        }

        public Builder httpVersion(HttpVersion httpVersion) {
            response.httpVersion = httpVersion;
            return this;
        }

        public Builder responsePhase(String responsePhase) {
            response.responsePhase = responsePhase;
            return this;
        }

        public Builder addHeader(String header, String value) {
            response.addHeader(header, value);
            return this;
        }

        public Builder setBody(byte[] body) {
            response.setMessage(body);
            return this;
        }

        public HttpResponse build() {
            if (response.getMessageBody() != null) {
                response.addHeader(HttpHeader.CONTENT_LENGTH.headerName,
                        String.valueOf(response.getMessageBody().length));
            }

            return response;
        }

    }

}
