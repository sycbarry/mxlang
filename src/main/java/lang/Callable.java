
//package com.sms.lang;
package lang;

import java.util.List;

public interface Callable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
