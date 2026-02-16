package com.tlkoushik.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Json {

    private static ObjectMapper myObjectMapper = getDefaultObjectMapper();

    private static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper objmapper = new ObjectMapper();
        objmapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objmapper;
    }

    public static <A> A jsonToObject(JsonNode node, Class<A> clazz)
            throws JsonProcessingException, IllegalArgumentException {
        return myObjectMapper.treeToValue(node, clazz);
    }

    public static JsonNode stringToJsonNode(String jsonString) throws JsonMappingException, JsonProcessingException {
        return myObjectMapper.readTree(jsonString);
    }

    public static JsonNode objectToJsonNode(Object obj) {
        return myObjectMapper.valueToTree(obj);
    }

    public static String stringify(Object obj) throws JsonProcessingException {
        ObjectWriter writer = myObjectMapper.writer();
        writer.with(SerializationFeature.INDENT_OUTPUT);
        return writer.writeValueAsString(obj);
    }
}
