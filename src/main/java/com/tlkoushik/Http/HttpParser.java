package com.tlkoushik.Http;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpParser {

    private static final int MAX_REQUEST_LINE = 4096;
    private static final int MAX_HEADER_LINE = 8192;
    private static final int MAX_HEADERS_SIZE = 32768;

    public static HttpRequest parse(InputStream in) throws IOException, HttpParsingException {
        HttpRequest request = new HttpRequest();

        parseRequestLine(in, request);
        parseHeaders(in, request);
        parseBody(in, request);
        return request;
    }

    // ================= REQUEST LINE =================

    private static void parseRequestLine(InputStream in, HttpRequest req) throws IOException, HttpParsingException {
        String line = readLine(in, MAX_REQUEST_LINE);

        String[] parts = line.split(" ");
        if (parts.length != 3)
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

        req.setMethod(parts[0]);
        req.setTarget(parts[1]);

        if (!parts[2].matches("HTTP/1\\.[01]"))
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

        req.setVersion(parts[2]);
    }

    // ================= HEADERS =================

    private static void parseHeaders(InputStream in, HttpRequest req) throws IOException, HttpParsingException {
        int totalSize = 0;

        while (true) {
            String line = readLine(in, MAX_HEADER_LINE);

            if (line.isEmpty())
                return; // End of headers

            totalSize += line.length();
            if (totalSize > MAX_HEADERS_SIZE)
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_413_BAD_REQUEST); // Request Header Fields
                                                                                             // Too
                                                                                             // Large

            int colon = line.indexOf(':');
            if (colon <= 0)
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

            String name = line.substring(0, colon).trim();
            String value = line.substring(colon + 1).trim();

            req.addHeader(name, value);
        }
    }

    // ================= BODY =================

    private static void parseBody(InputStream in, HttpRequest req) throws IOException, HttpParsingException {
        // String transferEncoding = req.getHeader("Transfer-Encoding");
        // String contentLength = req.getHeader("Content-Length");

        // if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked"))
        // {
        // if (contentLength != null)
        // throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        // // Smuggling protection

        // req.setBody(readChunkedBody(in));
        // return;
        // }

        // if (contentLength != null) {
        // int length;
        // try {
        // length = Integer.parseInt(contentLength);
        // } catch (NumberFormatException e) {
        // throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        // }

        // byte[] body = in.readNBytes(length);
        // if (body.length != length)
        // throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

        // req.setBody(body);
        // }

    }

    // ================= CHUNKED DECODER =================

    private static byte[] readChunkedBody(InputStream in) throws IOException, HttpParsingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while (true) {
            String sizeLine = readLine(in, 100);
            int size;

            try {
                size = Integer.parseInt(sizeLine.trim(), 16);
            } catch (NumberFormatException e) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            if (size == 0) {
                readLine(in, 100); // Final CRLF after last chunk
                break;
            }

            byte[] chunk = in.readNBytes(size);
            if (chunk.length != size)
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

            out.write(chunk);

            String crlf = readLine(in, 2);
            if (!crlf.isEmpty())
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        return out.toByteArray();
    }

    // ================= LINE READER =================

    private static String readLine(InputStream in, int maxLength) throws IOException, HttpParsingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int prev = -1;
        int curr;

        while ((curr = in.read()) != -1) {
            if (buffer.size() > maxLength)
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_414_BAD_REQUEST);

            if (prev == '\r' && curr == '\n') {
                byte[] lineBytes = buffer.toByteArray();
                return new String(lineBytes, 0, lineBytes.length - 1, StandardCharsets.US_ASCII);
            }

            buffer.write(curr);
            prev = curr;
        }

        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST); // Unexpected EOF
    }
}
