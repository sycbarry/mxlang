//package com.sms.lang;
package lang;

import java.util.List;
import java.util.Map;

public class Class implements Callable {

    public final String name;
    public Map<String, Function> methods;

    public Class(String name, Map<String, Function> methods) {
        this.methods = methods;
        this.name = name;
    }

    public Function findMethod(String name) {
        if(methods.containsKey(name)) {
            return methods.get(name);
        }
        return null;
    }

    public void defineMethod(String name, Function method) {
        methods.put(name, method);
    }

    @Override
    public String toString() {
        return "Class name: " + name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Instance instance = new Instance(this);
        Function initializer = findMethod("init");
        if(initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        Function initializer = findMethod("init");
        if(initializer == null) return 0;
        return initializer.arity();
    }
}
