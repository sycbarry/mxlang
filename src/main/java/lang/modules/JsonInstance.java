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
import java.util.Arrays;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;





public class JsonInstance extends Instance {

    Map<String, Object> _json;
    String raw;
    private static boolean DEBUG;

    public JsonInstance(Class klass,
                        Map<String, Object> map,
                        String rawJson,
                        boolean debug) {
        super(klass);
        _json = map;
        raw = rawJson;
        DEBUG = debug;
    }

    public Object get(Token name) {
        if(name.lexeme.equals("get")) {
            return new Callable() {
                @Override
                public int arity() { return 1; }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    String key = (String) arguments.get(0);
                    return _json.get(key);
                }
                @Override
                public String toString() { return "<native fn>"; }
            };
        }
        if(name.lexeme.equals("getFromPath")) {
            return new Callable() {
                @Override
                public int arity() { return 2;  }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    String value = (String) arguments.get(0);
                    boolean debug = (boolean) arguments.get(1);
                    // JsonToMapConverter converter = new JsonToMapConverter();
                    // Map<String, Object> map = converter.parse(_json.toString());
                    return getFromPath(_json, value, debug);
                }
            };
        }
        if(name.lexeme.equals("raw")) {
            return new Callable() {
                @Override
                public int arity() { return 0;  }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return raw;
                }
            };
        }
        if(name.lexeme.equals("toString")) {
            return new Callable() {
                @Override
                public int arity() { return 0;  }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return _json.toString();
                }
            };
        }
        return super.get(name);
    }

    /*
    takes the parsed json, and searches for a value on a certain path.
    */
    public static Object getFromPath(Map<String, Object> data, String path, boolean debug) {

        DEBUG = debug;

        // take the path input and split it appropriately by .
        String[] keys = path.split("\\.");

        // set the current data.
        Object current = data;

        // iterate through each input key.
        for (String key : keys) {
            if( current instanceof Map )  {
                if(DEBUG) { System.out.println("current map -> \n" + current + "\n"); }
                current = ((Map<?, ?>) current).get(key);
            }
            else if ( current instanceof List ) {
                if(DEBUG) { System.out.println("current list -> \n" + current + "\n"); }
                List<?> list = (List<?>) current;
                int index;
                try {
                    index = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    return null; // Invalid index
                }
                if (index < 0 || index >= list.size()) {
                    return null; // Index out of bounds
                }
                current = list.get(index);
            }
        }

        if ( current instanceof JsonPrimitive ) {
            if ( DEBUG == true ) { System.out.println("return value -> " + current); }
            return convertJsonPrimitive( (JsonPrimitive) current );
        }

        if(DEBUG == true) {
            StringBuilder builder = new StringBuilder();
            builder.append("found no value for path -> \n");
            builder.append(path);
            System.out.println(builder.toString());
        }

        return null;
    }

    /*
    converts each json value to its correct primitive value
    */
    public static Object convertJsonPrimitive(JsonPrimitive jsonPrimitive) {
        if(jsonPrimitive.isBoolean()) return jsonPrimitive.getAsBoolean();
        if(jsonPrimitive.isNumber()) return number(jsonPrimitive);
        if(jsonPrimitive.isString()) return jsonPrimitive.getAsString();
        return null;
    }

    /*
    returns the raw json as returned from the http request.
    */
    public String getRawJson() {
        return raw;
    }

    /*
    returns the parsed json ready to be traversed during key search.
    */
    public Map<String, Object> getParsedJson() {
        JsonToMapConverter converter = new JsonToMapConverter();
        Map<String, Object> map = converter.parse(_json.toString());
        return map;
    }


    private static Object number(JsonPrimitive p) {
        if(p.getAsByte()==4) {
            return p.getAsInt();
        }
        return p.getAsDouble();
    }




}
