package lang.packages.mas;

import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;

import java.util.List;

public class MASInstance extends Instance {

    public MASLoggingInstance masLoggingInstance;

    public MASInstance(Class klass) {
        super(klass);
    }

    public Object get(Token name) {
        if (name.lexeme.equals("init")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public MASInstance call(Interpreter interpreter, List<Object> arguments) {
                    MASInstance instance = new MASInstance(new Class("masinstance", null));
                    return instance;
                };
            };
        }
        if (name.lexeme.equals("logging")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public MASLoggingInstance call(Interpreter interpreter, List<Object> arguments) {
                    masLoggingInstance = new MASLoggingInstance(
                            new Class("maslogginginstance", null));
                    return masLoggingInstance;
                }
            };
        }
        return super.get(name);
    }

    public MASInstance newInstance(List<Object> objects) {
        MASInstance instance = new MASInstance(new Class("mas", null));
        return instance;
    }

}
