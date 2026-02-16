package com.tlkoushik.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;
import java.net.SocketException;

import com.tlkoushik.Http.HttpHeader;
import com.tlkoushik.Http.HttpParser;
import com.tlkoushik.Http.HttpParsingException;
import com.tlkoushik.Http.HttpRequest;
import com.tlkoushik.Http.HttpResponse;
import com.tlkoushik.Http.HttpStatusCode;
import com.tlkoushik.Http.HttpVersion;
import com.tlkoushik.core.IO.ReadFileException;
import com.tlkoushik.core.IO.WebRootHandler;

public class HttpConnectionWorkerThread implements Runnable {
    private Socket connSocket;
    private WebRootHandler webRootHandler;

    static final int MAX_REQUESTS_PER_CONNECTION = 100;

    public HttpConnectionWorkerThread(Socket con, WebRootHandler webRootHandler) {
        this.connSocket = con;
        this.webRootHandler = webRootHandler;
    }

    @Override
    public void run() {

        try (InputStream input = connSocket.getInputStream();
                OutputStream output = connSocket.getOutputStream()) {

            boolean keepAlive = true;
            int requestHandled = 0;

            while (keepAlive && requestHandled < MAX_REQUESTS_PER_CONNECTION) {

                HttpRequest request;

                try {
                    request = HttpParser.parse(input);
                } catch (Exception e) {
                    break; // malformed request → close
                }

                HttpResponse response = handlerequest(request);

                keepAlive = shouldKeepAlive(request);

                if (keepAlive) {
                    connSocket.setSoTimeout(15000);
                    response.addHeader("Connection", "keep-alive");
                } else {
                    response.addHeader("Connection", "close");
                }
                connSocket.setKeepAlive(keepAlive);
                output.write(response.getResponseBytes());
                output.flush();

                requestHandled += 1;
            }

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());

        } finally {
            try {
                connSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private boolean shouldKeepAlive(HttpRequest request) {
        String connectionHeader = request.getHeaderValue("connection");

        if (HttpVersion.HTTP_1_1.equals(request.getVersion())) {
            if (connectionHeader.equalsIgnoreCase("close")) {
                return false;
            }
            return true;
        }

        if (connectionHeader.equalsIgnoreCase("keep-alive")) {
            return true;
        }
        return false;
    }

    private HttpResponse handlerequest(HttpRequest request) {
        switch (request.getMethod()) {
            case GET:
                return handleGetRequest(request, true);
            case HEAD:
                return handleGetRequest(request, false);
            default:
                return new HttpResponse.Builder()
                        .httpVersion((request.getVersion()))
                        .statusCode(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED)
                        .build();
        }
    }

    private HttpResponse handleGetRequest(HttpRequest request, boolean setMessage) {

        try {
            HttpResponse.Builder responseBuilder = new HttpResponse.Builder()
                    .httpVersion(request.getVersion())
                    .statusCode(HttpStatusCode.OK)
                    .addHeader(HttpHeader.CONTENT_TYPE.headerName, webRootHandler.getFileMimeType(request.getTarget()));
            if (setMessage) {
                byte[] messageBody = webRootHandler.getFileByteArray(request.getTarget());
                responseBuilder.addHeader(HttpHeader.CONTENT_LENGTH.headerName, String.valueOf(messageBody.length))
                        .setBody(messageBody);
            }

            return responseBuilder.build();

        } catch (FileNotFoundException e) {
            return new HttpResponse.Builder().httpVersion(request.getVersion())
                    .statusCode(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND).build();
        } catch (ReadFileException e) {
            return new HttpResponse.Builder().httpVersion(request.getVersion())
                    .statusCode(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR).build();
        }

    }

}
