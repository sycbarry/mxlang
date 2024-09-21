
//package com.sms.lang.packages.mxtest;
//import com.sms.lang.modules.*;
//import com.sms.lang.Callable;
//import com.sms.lang.Instance;
//import com.sms.lang.Class;
//import com.sms.lang.Interpreter;
//import com.sms.lang.Token;

package lang.packages.mxtest;
import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MXTestInstance extends Instance {

    private String APIKEY;
    private String MAXAUTH;
    private String OSLCENDPOINT;
    private String RESTENDPOINT; // TODO
    private String APIENDPOINT;  // TODO
    private String USERNAME;
    private String PASSWORD;
    public MXTestHttpOslc mxTestHttpOslc;
    public MXTestHttpApi mxTestHttpApi;
    //private MaxHttp.MaxHttpRest maxHttpRest;
    //private MaxHttp.MaxHttpAPI  maxHttpApi;

    public MXTestInstance(Class klass) {
        super(klass);
        // http = new HttpInstance(new Class("http", null));
    }

    public Object get(Token name) {

        if(name.lexeme.equals("init")) {
            return new Callable() {
                @Override
                public int arity() { return -1;}
                @Override
                public MXTestInstance call(Interpreter interpreter, List<Object> arguments) {
                    // do some more init things here...
                    if( arguments.size() > 0 )  {
                        return newInstance(arguments);
                    }
                    else {
                        return new MXTestInstance( new Class("mbo", null) );
                    }
                }
            };
        }
        if( name.lexeme.equals("getMaxAuth") ) {
            return new Callable() {
                @Override
                public int arity () { return 0;  }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return getMaxAuth();
                }
            };
        }
        if( name.lexeme.equals("getApiEndpoint") ) {
            return new Callable() {
                @Override
                public int arity () { return 0;  }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return getApiEndpoint();
                }
            };
        }
        if( name.lexeme.equals("getApiKey") ) {
            return new Callable() {
                @Override
                public int arity () { return 0;  }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return getApiKey();
                }
            };
        }
        if(name.lexeme.equals("setMaxAuth")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setMaxAuth ( (String) arguments.get(0) );
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if(name.lexeme.equals("setPassword")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setPassword ( (String) arguments.get(0) );
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if(name.lexeme.equals("setUsername")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setUsername ( (String) arguments.get(0) );
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if(name.lexeme.equals("setApiEndpoint")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setApiEndpoint((String)arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if(name.lexeme.equals("setRestEndpoint")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setRestEndpoint((String)arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if(name.lexeme.equals("setOslcEndpoint")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setOslcEndpoint((String)arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if(name.lexeme.equals("setApiKey")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setApiKey((String)arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if(name.lexeme.equals("getApiKey")) {
            return new Callable() {
                @Override
                public int arity () { return 0;  }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return getApiKey();
                }
            };
        }

        if(name.lexeme.equals("oslc")) {
            mxTestHttpOslc = new MXTestHttpOslc(new Class("maxhttposlc", null),
                    OSLCENDPOINT, APIKEY, MAXAUTH, USERNAME, PASSWORD);
            return mxTestHttpOslc;
        }
        if(name.lexeme.equals("api")) {
            mxTestHttpApi = new MXTestHttpApi(new Class("maxhttposlc", null),
                    APIENDPOINT, APIKEY, MAXAUTH, USERNAME, PASSWORD);
            return mxTestHttpApi;
        }

        return super.get(name);
    }


    public MXTestInstance newInstance(List<Object> objects) {
        MXTestInstance instance = new MXTestInstance( new Class("mbo",null) );
        for( Object argument : objects ) {
            for ( Object key: ( (HashMap) argument ).keySet() ) {
                String _key = (String) key;
                String _value = (String) ( (HashMap) argument ).get(_key);
                switch(_key) {
                    case "apikey":
                        try {
                            instance.setApiKey(_value);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        break;
                    case "maxauth":
                        try {
                            instance.setMaxAuth(_value);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        break;
                    case "apiendpoint":
                        try {
                            instance.setApiEndpoint(_value);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        break;
                    case "restendpoint":
                        try {
                            instance.setRestEndpoint(_value);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        break;
                    case "oslcendpoint":
                        try {
                            instance.setOslcEndpoint(_value);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        break;
                    case "username":
                        try {
                            instance.setUsername(_value);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        break;
                    case "password":
                        try {
                            instance.setPassword(_value);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        break;
                    default:
                        break;

                }

            }
        }
        // maxHttpRest = new MaxHttp.MaxHttpRest(endpoint, apiKey, userName, password);
        // maxHttpAPI  = new MaxHttp.MaxHttpAPI(endpoint, apiKey, userName, password);
        return instance;
    }

    public Void setPassword(String password) throws Exception{
        if( ! ( password instanceof String ) ) {
            throw new Exception("Api Key must be a string value");
        }
        PASSWORD = password;
        return null;
    }
    public Void setUsername(String userName) throws Exception{
        if( ! ( userName instanceof String ) ) {
            throw new Exception("Api Key must be a string value");
        }
        USERNAME = userName;
        return null;
    }
    public Void setApiEndpoint(String endpoint) throws Exception{
        if( ! ( endpoint instanceof String ) ) {
            throw new Exception("Api Key must be a string value");
        }
        APIENDPOINT = endpoint;
        return null;
    }
    public Void setRestEndpoint(String endpoint) throws Exception{
        if( ! ( endpoint instanceof String ) ) {
            throw new Exception("Api Key must be a string value");
        }
        RESTENDPOINT = endpoint;
        return null;
    }
    public Void setOslcEndpoint(String endpoint) throws Exception{
        if( ! ( endpoint instanceof String ) ) {
            throw new Exception("Api Key must be a string value");
        }
        OSLCENDPOINT = endpoint;
        return null;
    }
    public Void setApiKey(String apiKey) throws Exception {
        if( ! ( apiKey instanceof String ) ) {
            throw new Exception("Api Key must be a string value");
        }
        APIKEY = apiKey;
        return null;
    }
    public Void setMaxAuth(String maxAuth) throws Exception {
        if( ! ( maxAuth instanceof String ) ) {
            throw new Exception("max auth token must be a string value");
        }
        MAXAUTH = maxAuth;
        return null;
    }

    public String getMaxAuth() {
        return MAXAUTH;
    }

    public String getApiEndpoint() {
        return APIENDPOINT;
    }

    public String getApiKey() {
        return APIKEY;
    }


}
