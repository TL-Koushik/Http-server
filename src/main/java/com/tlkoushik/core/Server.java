package com.tlkoushik.core;

import com.tlkoushik.core.IO.WebRootHandler;
import com.tlkoushik.core.IO.WebRootNotFoundExceptipn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server {

    private static ServerSocket serverSocket;
    private static ThreadPoolExecutor executor;
    private static WebRootHandler webRootHandler;

    public static void createServer(int port, String webroot) {

        try {
            serverSocket = new ServerSocket(port);
            webRootHandler = new WebRootHandler(webroot);

            executor = new ThreadPoolExecutor(
                    50, // core threads (always alive)
                    200, // max concurrent worker threads
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(2000)// waiting tasks
            // new ThreadPoolExecutor.AbortPolicy()
            );

            System.out.println("Server created on port " + port);

        } catch (IOException e) {
            System.out.println("Error creating server socket");
            e.printStackTrace();

        } catch (WebRootNotFoundExceptipn e) {
            System.out.println("Webroot not found: " + webroot);
            e.printStackTrace();
        }
    }

    public static void startListening() {

        if (serverSocket == null) {
            System.out.println("Server not initialized.");
            return;
        }

        System.out.println("Server listening...");

        try {
            while (!serverSocket.isClosed()) {

                Socket connSocket = serverSocket.accept();

                System.out.println(
                        "Client connected: " +
                                connSocket.getRemoteSocketAddress());

                connSocket.setSoTimeout(15000);

                executor.execute(
                        new HttpConnectionWorkerThread(connSocket, webRootHandler));
            }

        } catch (IOException e) {
            System.out.println("Error accepting connection");
            e.printStackTrace();

        } finally {
            shutdown();
        }
    }

    public static void shutdown() {
        System.out.println("Shutting down server...");

        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (executor != null)
            executor.shutdown();
    }
}
