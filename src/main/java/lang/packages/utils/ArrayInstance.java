
//package com.sms.lang.packages.utils;
//import com.sms.lang.Callable;
//import com.sms.lang.Instance;
//import com.sms.lang.Class;
//import com.sms.lang.Interpreter;
//import com.sms.lang.Token;

package lang.packages.utils;
import  lang.modules.*;
import  lang.Callable;
import  lang.Instance;
import  lang.Class;
import  lang.Interpreter;
import  lang.Token;


import java.util.List;
import java.util.ArrayList;

public class ArrayInstance extends Instance {

    private List<Object> list;

    public ArrayInstance(Class klass) {
        super(klass);
        list = new ArrayList<>();
    }

    public Object get(Token name) {

        if(name.lexeme.equals("listAll")) {
            return new Callable() {
                @Override
                public int arity() { return 0;}
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    listAll();
                    return null;
                }
            };
        }
        if(name.lexeme.equals("getAt")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return findAt(arguments.get(0));
                }
            };
        }

        if(name.lexeme.equals("copyFrom")) {
            return new Callable() {
                @Override
                public int arity() { return -1;}
                @Override
                public ArrayInstance call(Interpreter interpreter, List<Object> arguments) {
                    return copyfrom( (ArrayInstance)arguments.get(0) );
                }
            };
        }

        if(name.lexeme.equals("new")) {
            return new Callable() {
                @Override
                public int arity() { return -1;}
                @Override
                public ArrayInstance call(Interpreter interpreter, List<Object> arguments) {
                    return newList(arguments);
                }
            };
        }


        if(name.lexeme.equals("size")) {
            return new Callable() {
                @Override
                public int arity() { return 0;}
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return size();
                }
            };
        }

        // setat( value at index, value to );
        if(name.lexeme.equals("setAt")) {
            return new Callable() {
                @Override
                public int arity() { return 2;}
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return setat( (Object) arguments.get(0), (Object) arguments.get(1) );
                }
            };
        }

        if(name.lexeme.equals("append")) {
            return new Callable() {
                @Override
                public int arity() { return 1;}
                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return append( (Object) arguments.get(0));
                }
            };
        }

        return super.get(name);
    }

    public ArrayInstance copyfrom(ArrayInstance instance) {
        ArrayInstance newInstance = new ArrayInstance(new Class("array", null));
        int size = (int) instance.size().intValue();
        for ( int i = 0 ; i < size; i++ ) {
            newInstance.append( instance.findAt(i) );
        }
        return newInstance;
    }

    public Object findAt(Object position) {
        return list.get(( (Number) position).intValue());
    }

    public Void setat(Object position, Object value) {
        list.set(((Number) position).intValue(), value);
        return null;
    }

    public Double size() {
        return ((Number) list.size()).doubleValue();
    }

    public Void listAll() {
        for( Object object : list ) {
            if(object == null) {
                continue;
            }
            if(object instanceof Double) {
                System.out.println( ((Number) object).intValue() );
            }
            if(object instanceof String) {
                System.out.println(object);
            }
        }
        return null;
    }

    public Void append(Object object) { list.add(object); return null; }

    public ArrayInstance newList(List<Object> arguments) {
        if(arguments.size() <= 0) {
            return this;
        }
        for(Object object : arguments) {
            list.add(object);
        }
        return this;
    }

}
