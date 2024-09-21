package lang.packages.mas;

import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;

import java.util.List;

public class MASLoggingInstance extends Instance {

    public LoggingOpenShiftInstance loggingOpenShiftInstance;
    public LoggingApiInstance loggingApiInstance;

    public MASLoggingInstance(Class klass) {
        super(klass);
    }

    public Object get(Token name) {
        if (name.lexeme.equals("fromOpenShift")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public LoggingOpenShiftInstance call(Interpreter interpreter, List<Object> arguments) {
                    loggingOpenShiftInstance = new LoggingOpenShiftInstance(
                            new Class("loggingopenshiftinstance", null));
                    return loggingOpenShiftInstance;
                };
            };
        }
        if (name.lexeme.equals("fromApi")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public LoggingApiInstance call(Interpreter interpreter,
                        List<Object> arguments) {
                    loggingApiInstance = new LoggingApiInstance(
                            new Class("loggingapinstance", null));
                    return loggingApiInstance;
                };
            };
        }
        return super.get(name);
    }

    public MASLoggingInstance newInstance(List<Object> objects) {
        MASLoggingInstance instance = new MASLoggingInstance(new Class("maslogging", null));
        return instance;
    }

}
