
//package com.sms.lang.packages.utils;
//import com.sms.lang.Callable;
//import com.sms.lang.Instance;
//import com.sms.lang.Class;
//import com.sms.lang.Interpreter;
//import com.sms.lang.Token;
//import com.sms.lang.Expr;
//import com.sms.lang.packages.utils.ArrayInstance;

package lang.packages.utils;
import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;
import lang.packages.utils.ArrayInstance;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DictionaryInstance extends Instance {


    private Map<Object, Object> map;

    public DictionaryInstance(Class klass) {
        super(klass);
        map = new HashMap<>();
    }

    public Object get(Token name) {

        if(name.lexeme.equals("new")) {
            return new Callable() {
                @Override
                public int arity() { return -1; }
                @Override
                public DictionaryInstance call(Interpreter interpreter, List<Object> arguments) {
                    return newDictionary(arguments);
                }
            };
        }


        if(name.lexeme.equals("containsKey")) {
            return new Callable() {
                @Override
                public int arity() { return 1; }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return containsKey(arguments.get(0));
                }
            };
        }


        if(name.lexeme.equals("getValueFromKey")) {
            return new Callable() {
                @Override
                public int arity() { return 1; }
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return getValueFromKey(arguments.get(0));
                }
            };
        }

        if(name.lexeme.equals("put")) {
            return new Callable() {
                @Override
                public int arity() { return 2; }
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        put(arguments.get(0), arguments.get(1));
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            };
        }



        return super.get(name);
    }

    public void put(Object a, Object b) throws Exception {
        if(map.containsKey(a)) {
            throw new Exception(" cannot insert key that already exists");
        }
        map.put(a, b);
    }

    public boolean containsKey(Object object) {
        return map.containsKey(object);
    }


    public Object getValueFromKey(Object object) {
        if (object instanceof Integer) {
            return map.get((Double) object);
        }
        // Add other type checks if needed
        else {
            return map.get(object);
        }
    }

    public DictionaryInstance newDictionary(List<Object> arguments) {
        for( Object argument : arguments ) {
            for ( Object key: ( (HashMap) argument  ).keySet() ) {
                Object _key = key;
                Object _value = ((HashMap)argument).get(_key);
                map.put(_key, _value);
            }
        }
        return this;
    }

}
