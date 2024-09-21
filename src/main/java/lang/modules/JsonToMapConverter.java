//package com.sms.lang.modules;
package lang.modules;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JsonToMapConverter {

    public static Map<String, Object> jsonToMap(JsonElement element) {
        if (element.isJsonObject()) {
            return processObject(element.getAsJsonObject());
        }
        else if (element.isJsonArray()) {
            return Map.of("array", processArray(element.getAsJsonArray()));
        }
        else if (element.isJsonPrimitive()) {
            return Map.of("value", element.getAsJsonPrimitive());
        }
        else if (element.isJsonNull()) {
            return Map.of("null", null);
        }
        throw new IllegalArgumentException("Unsupported JSON element type");
    }

    private static Map<String, Object> processObject(JsonObject obj) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            map.put(entry.getKey(), jsonToMap(entry.getValue()));
        }
        return map;
    }

    private static List<Object> processArray(JsonArray array) {
        return StreamSupport.stream(array.spliterator(), false)
            .map(JsonToMapConverter::jsonToMap)
            .collect(Collectors.toList());
    }

    public static Map<String, Object> parse(String input) {
        Map<String, Object> result = new HashMap<>();
        // Remove the outermost curly braces and split the string
        //String[] keyValuePairs = input.substring(1, input.length() - 1).split(", (?=\\w+=\\{)");
        String[] keyValuePairs = input.substring(
                1, input.length() - 1).split(",");

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            if (value.startsWith("{")) {
                // Recursive call for nested map
                result.put(key, parse(value));
            } else if (value.startsWith("[")) {
                // Recursive call for list
                result.put(key, parseList(value));
            } else {
                // Handle primitive value
                result.put(key, parsePrimitive(value));
            }
        }
        return result;
    }

    private static List<Object> parseList(String input) {
        List<Object> list = new ArrayList<>();
        // Remove the square brackets
        String[] items = input.substring(1, input.length() - 1).split(", (?=\\{)");
        for (String item : items) {
            if (item.startsWith("{")) {
                list.add(parse(item));
            } else {
                list.add(parsePrimitive(item));
            }
        }
        return list;
    }

    private static Object parsePrimitive(String input) {
        // Remove the "value=" part and any extra quotes
        String value = input.replaceAll("value=", "").replaceAll("^\"|\"$", "").trim();
        // Here you can add more logic to handle different types of primitives
        return value;
    }

}
