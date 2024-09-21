//package com.sms.lang.modules;
//import com.sms.lang.Callable;
//import com.sms.lang.Interpreter;
//import com.sms.lang.Function;
//import com.sms.lang.Instance;
//import com.sms.lang.Token;
//import com.sms.lang.Class;

package lang.modules;

import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;
import lang.packages.utils.ArrayInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class HttpResponseInstance extends Instance {

    int statusCode;
    Object body;

    public HttpResponseInstance(Class klass, int statusCode, Object body) {
        super(klass);
        this.statusCode = statusCode;
        this.body = body;
    }

    public Object get(Token name) {
        if (name.lexeme.equals("getStatusCode")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Double call(Interpreter interpreter, List<Object> arguments) {
                    Double d = new Double(statusCode);
                    return (Double) d;
                }
            };
        }
        if (name.lexeme.equals("getResponseBody")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Map<String, Object> map = toJsonObject(body.toString());
                    if (map == null) {
                        return body.toString();
                    }
                    return (JsonInstance) new JsonInstance(
                            new Class("jsoninstance", null),
                            map,
                            body.toString(),
                            false);
                }
            };
        }
        return super.get(name);
    }

    public Map<String, Object> toJsonObject(String jsonString) {
        try {
            JsonElement element = JsonParser.parseString(jsonString);
            JsonToMapConverter jsonMapper = new JsonToMapConverter();
            Map<String, Object> result = jsonMapper.jsonToMap(element);
            return result;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

}
