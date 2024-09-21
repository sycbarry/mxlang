// package com.sms.lang;
package lang;

import java.util.List;

public class Function implements Callable {

    public final Stmt.Function declaration;
    public final Environment closure;
    public final boolean isInitializer;

    public Function(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    Function bind(Instance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new Function(declaration, environment, isInitializer);
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // each function gets its own environment.
        Environment environment = new Environment(closure);
        // here we find the function's arguments to parameters.
        for(int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }
        // when we execute a function and return something prior to the child block finishing
        // we can treat that as an exception and just return the exception's value
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch(Return returnValue) {
            if(isInitializer) return closure.getAt(0, "this");
            return returnValue.value;
        }
        if(isInitializer) return closure.getAt(0, "this");
        return null;
    }

}
