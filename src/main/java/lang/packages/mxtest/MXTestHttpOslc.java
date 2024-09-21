
package lang.packages.mxtest;
import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;

import java.util.Base64;

import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class MXTestHttpOslc extends Instance {

    final String ENDPOINT;
    final String APITOKEN;
    final String USERNAME;
    final String PASSWORD;
    final String MAXAUTH;
    HttpInstance http;

    public MXTestHttpOslc(Class klass,
                          String endpoint,
                          String apiToken,
                          String maxAuth,
                          String userName,
                          String password) {

        super(klass);
        this.ENDPOINT = endpoint;
        this.MAXAUTH = maxAuth;
        this.APITOKEN = apiToken;
        this.USERNAME = userName;
        this.PASSWORD = password;
        this.http = new HttpInstance( new Class("http", null) );
    }

    public Object get(Token name) {

        if(name.lexeme.equals("ping")) {
            return new Callable() {
                @Override
                public int arity() { return 0;}
                @Override
                public HttpResponseInstance call(Interpreter interpreter, List<Object> arguments) {
                    HashMap<String, String> headers = new HashMap<>();
                    if(APITOKEN != null) {
                        headers.put("APIKEY", APITOKEN);
                    }
                    else if(MAXAUTH != null) {
                        headers.put("MAXAUTH", MAXAUTH);
                    }
                    else if(USERNAME != null && PASSWORD != null) {
                        // base 64 encode this baby.
                        StringBuilder builder = new StringBuilder();
                        builder.append(USERNAME);
                        builder.append(":");
                        builder.append(PASSWORD);
                        String b64Encoded = Base64.getEncoder().encodeToString(builder.toString().getBytes());
                        headers.put("Authorization", "Basic " + b64Encoded);
                    }
                    http.setHeadersFromMap(headers);
                    return http.ping(ENDPOINT);
                }
            };
        }
        if(name.lexeme.equals("postjson")) {
            return new Callable() {
                @Override
                public int arity() { return 1; }
                @Override
                // TODO return a http response instance.
                // contains a status code
                // contains a body of a jsoninstance.
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type" , "application/json");
                    headers.put("Accept" , "application/json");
                    if(APITOKEN != null) {
                        headers.put("APIKEY", APITOKEN);
                    }
                    else if(MAXAUTH != null) {
                        headers.put("MAXAUTH", MAXAUTH);
                    }
                    else if(USERNAME != null && PASSWORD != null) {
                        // base 64 encode this baby.
                        StringBuilder builder = new StringBuilder();
                        builder.append(USERNAME);
                        builder.append(":");
                        builder.append(PASSWORD);
                        String b64Encoded = Base64.getEncoder().encodeToString(builder.toString().getBytes());
                        headers.put("Authorization", "Basic " + b64Encoded);
                    }
                    Object data = http.post(ENDPOINT, (String) arguments.get(0), headers);
                    return data;
                }
            };
        }
        if(name.lexeme.equals("getjson")) {
            return new Callable() {
                @Override
                public int arity() { return 0;  }
                @Override
                public HttpResponseInstance call(Interpreter interpreter, List<Object> arguments) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type" , "application/json");
                    headers.put("Accept" , "application/json");
                    if(APITOKEN != null) {
                        headers.put("APIKEY", APITOKEN);
                    }
                    else if(MAXAUTH != null) {
                        headers.put("MAXAUTH", MAXAUTH);
                    }
                    else if(USERNAME != null && PASSWORD != null) {
                        // base 64 encode this baby.
                        StringBuilder builder = new StringBuilder();
                        builder.append(USERNAME);
                        builder.append(":");
                        builder.append(PASSWORD);
                        String b64Encoded = Base64.getEncoder().encodeToString(builder.toString().getBytes());
                        headers.put("Authorization", "Basic " + b64Encoded);
                    }
                    http.setHeadersFromMap(headers);
                    HttpResponseInstance data = http.get(ENDPOINT);
                    // Map<String, Object> map = http.toJsonObject(data.toString());
                    // JsonInstance jsonInstance = new JsonInstance(new Class("jsoninstance", null), map);
                    return data;
                }
            };
        }

        return super.get(name);
    }

}
