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


public class HttpInstance extends Instance {

    public Map<String, String> headers = new HashMap<>();

    public HttpInstance(Class klass) {
        super(klass);
    }

    public Object get(Token name) {
        if(name.lexeme.equals("ping")) {
            return new Callable() {
                @Override
                public int arity() { return 1; }
                @Override
                public HttpResponseInstance call(Interpreter interpreter, List<Object> arguments) {
                    HttpResponseInstance response = ping( (String) arguments.get(0));
                    return response;
                }
                @Override
                public String toString() { return "<native fn>"; }
            };
        }
        if(name.lexeme.equals("setHeaders")) {
            return new Callable() {
                @Override
                public int arity() { return -1; }
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    return setHeaders(arguments);
                }
            };
        }
        if(name.lexeme.equals("get")) {
            return new Callable() {
                @Override
                public int arity() { return 1; }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Object response = get( (String) arguments.get(0));
                    // Map<String, Object> map = toJsonObject(response.toString());
                    return response;
                }
                @Override
                public String toString() { return "<native fn>"; }
            };
        }
        if(name.lexeme.equals("getJson")) {
            return new Callable(){
                @Override
                public int arity() { return 0;  }
                @Override
                public HttpResponseInstance call(Interpreter interpreter,List<Object> arguments) {
                    HttpResponseInstance responseInstance = get( (String) arguments.get(0));
                    // Map<String, Object> map = toJsonObject(response.toString());
                    return responseInstance;
                }
            };
        }
        if(name.lexeme.equals("post")) {
            return new Callable() {
                @Override
                public int arity() { return 3; }
                @Override
                public HttpResponseInstance call(Interpreter interpreter, List<Object> arguments) {
                    // endpoint, body, headers.
                    String endpoint = (String) arguments.get(0);
                    String body = (String) arguments.get(1);
                    // unsafe here?
                    HashMap<String, String> _headers = (HashMap<String, String>) arguments.get(2);
                    return post(endpoint, body, _headers);
                }
            };
        }
        return super.get(name);
    }

    /*
    returns the parsed json object ready for traversal.
    */
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



    public HttpResponseInstance post(String endpoint, String body, HashMap<String, String> headers) {
        try {
            HttpURLConnection connection = ( HttpURLConnection ) new URL(endpoint).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            for(String key : headers.keySet()) {
                connection.setRequestProperty( (String) key, (String) headers.get(key));
            }
            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(body);
            osw.flush();
            osw.close();
            os.close();
            connection.connect();

            int responseCode = (int) connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer responseBody = new StringBuffer();
            while( (inputLine = in.readLine()) != null ) {
                responseBody.append(inputLine);
            }
            in.close();
            HttpResponseInstance responseInstance = new HttpResponseInstance(
                    new Class("httpresponseinstance", null),
                    responseCode,
                    responseBody);
            return responseInstance;
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

    public Void setHeadersFromMap(HashMap<String, String> _headers) {
        headers =  _headers;
        return null;
    }


    public Void setHeaders(List<Object> _headers) {
    //    HashMap<String, String>
        // headers = _headers;
        return null;
    }

    public HttpResponseInstance ping(String endpoint) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("HEAD");
            if(headers != null) {
                // set the headers.
                for( String key : headers.keySet()  ){
                    connection.setRequestProperty(
                            (String) key,
                            (String) headers.get(
                                (String)key
                            ));
                }
            }
            int responseCode = connection.getResponseCode();
            HttpResponseInstance responseInstance = new HttpResponseInstance(new Class("httpresponseinstance", null),
                responseCode,
                null
            );
            return responseInstance;
        } catch ( Exception e ) {
            e.printStackTrace();
            HttpResponseInstance responseInstance = new HttpResponseInstance(new Class("httpresponseinstance", null),
                500,
                null
            );
            return responseInstance;
        }
    }

    public HttpResponseInstance get(String endpoint) {
        try {
            HttpURLConnection connection = ( HttpURLConnection ) new URL(endpoint).openConnection();
            connection.setRequestMethod("GET");
            if(headers != null) {
                // set the headers.
                for( String key : headers.keySet()  ){
                    connection.setRequestProperty(
                            (String) key,
                            (String) headers.get(
                                (String)key
                            ));
                }
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            int responseCode = (int) connection.getResponseCode();
            StringBuffer responseBody = new StringBuffer();
            if(responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseBody.append(inputLine);
                }
                in.close();
            }
            HttpResponseInstance responseInstance = new HttpResponseInstance(new Class("httpresponseinstance", null),
                responseCode,
                responseBody
            );
            return responseInstance;
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

}
