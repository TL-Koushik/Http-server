package com.tlkoushik.config;

import java.io.FileReader;

import com.fasterxml.jackson.databind.JsonNode;

import com.tlkoushik.util.Json;

public class ConfigurationManager {
    private static Configuration myCurrentConfiguration;

    private ConfigurationManager() {
    }

    public static void loadConfiguration(String filePath) {
        try (FileReader filereader = new FileReader(filePath)) {
            StringBuffer sb = new StringBuffer();
            int i;
            try {
                while ((i = filereader.read()) != -1) {
                    sb.append((char) i);
                }
            } catch (Exception e) {
                throw new HttpConfigurationException(e);
            }
            JsonNode confJsonNode = null;
            try {
                confJsonNode = Json.stringToJsonNode(sb.toString());
                System.out.println(Json.stringify(confJsonNode));
                myCurrentConfiguration = Json.jsonToObject(confJsonNode, Configuration.class);
            } catch (Exception e) {
                throw new HttpConfigurationException(e);
            }

        } catch (Exception e) {
            throw new HttpConfigurationException(e);
        }
    }

    public static Configuration getConfiguration() {
        if (myCurrentConfiguration == null)
            throw new HttpConfigurationException("configuration not loaded");
        return myCurrentConfiguration;
    }
}
