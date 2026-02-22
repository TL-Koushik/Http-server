package com.tlkoushik;

import com.tlkoushik.config.Configuration;
import com.tlkoushik.config.ConfigurationManager;
import com.tlkoushik.core.Server;

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("Server Starting");
        if (args.length != 1) {
            System.out.println("path not provided or provided more than two argunments");
            System.exit(0);
        }
        String path = args[0].trim();
        ConfigurationManager.loadConfiguration(path);
        Configuration config = ConfigurationManager.getConfiguration();
        System.out.println("Running on " + config.getPort() + " port");
        System.out.println("Webroot : " + config.getWebroot());
        Server.createServer(config.getPort(), config.getWebroot());
        System.out.println("Server Started Listeing");
        Server.startListening();
        System.out.println("server ended");
    }
}