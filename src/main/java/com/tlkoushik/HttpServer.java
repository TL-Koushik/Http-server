package com.tlkoushik;

import com.tlkoushik.config.Configuration;
import com.tlkoushik.config.ConfigurationManager;
import com.tlkoushik.core.Server;

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("Server Starting");
        String path = "/home/luffy/coding/httpserver/src/main/resources/http.json";
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