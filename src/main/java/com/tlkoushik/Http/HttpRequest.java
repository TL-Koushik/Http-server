package com.tlkoushik.Http;

public class HttpRequest extends HttpMessage {
    private HttpMethod method;
    private String target;
    private String originalHttpVersion;
    private HttpVersion version;

    HttpRequest() {
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod method : HttpMethod.values()) {
            if (method.name().equals(methodName)) {
                this.method = method;
                return;
            }
        }
        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) throws HttpParsingException {
        if (target == null || target.length() == 0)
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        this.target = target;
    }

    public String getOriginalHttpVersion() {
        return originalHttpVersion;
    }

    public void setVersion(String originalHttpVersion) throws HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.version = HttpVersion.getBestCompaitabilHttpVersion(originalHttpVersion);
        if (this.version == null) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    public HttpVersion getVersion() {
        return version;
    }

}
