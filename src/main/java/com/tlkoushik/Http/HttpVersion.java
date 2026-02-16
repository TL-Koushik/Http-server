package com.tlkoushik.Http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0", 1, 0),
    HTTP_1_1("HTTP/1.1", 1, 1);

    public final String LITERAL;
    public final int MAJOR;
    public final int MINOR;

    private static final Pattern httpVersionRegexPattern = Pattern.compile("^HTTP/(?<major>\\d+)\\.(?<minor>\\d+)$");

    HttpVersion(String literal, int major, int minor) {
        this.LITERAL = literal;
        this.MAJOR = major;
        this.MINOR = minor;
    }

    public static HttpVersion getBestCompaitabilHttpVersion(String literal) throws HttpParsingException {
        Matcher matcher = httpVersionRegexPattern.matcher(literal);

        if (!matcher.find() || matcher.groupCount() != 2) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        int major = Integer.parseInt(matcher.group("major"));
        int minor = Integer.parseInt(matcher.group("minor"));
        HttpVersion tempHttpVersion = null;
        for (HttpVersion version : HttpVersion.values()) {
            if (version.LITERAL.equals(literal)) {
                return version;
            } else if (version.MAJOR == major && version.MINOR < minor) {
                tempHttpVersion = version;
            }
        }
        return tempHttpVersion;

    }
}
