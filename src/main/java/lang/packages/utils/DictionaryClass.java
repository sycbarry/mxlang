//package com.sms.lang.packages.utils;
//import com.sms.lang.Class;


package lang.packages.utils;
import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static java.util.Map.entry;


public class DictionaryClass {

    private final Map<String, Object> properties;

    public DictionaryClass () {
        properties = new HashMap<>();
    }

    //public double printthis(){ return -1; }

}
