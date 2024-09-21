//package com.sms.lang.modules;
//import com.sms.lang.Class;

package lang.modules;
import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;
import lang.packages.utils.ArrayInstance;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static java.util.Map.entry;



public class JsonClass {

    private final Map<String, Object> properties;

    public JsonClass () {
        properties = new HashMap<>();
    }

    //public double printthis(){ return -1; }

}
