package lang.packages.mas;

import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;
import java.util.List;

public class LoggingApiInstance extends Instance {

    public String configFile;
    public String stanza;
    public String configPath;
    public String nameSpace;
    public String podName;

    public LoggingApiInstance(Class klass) {
        super(klass);
    }

    public Object get(Token name) {
        if (name.lexeme.equals("generateLogs")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    generateLogs();
                    return null;
                }
            };
        }
        if (name.lexeme.equals("setConfigFile")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setConfigFile((String) arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if (name.lexeme.equals("setStanza")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setStanza((String) arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        return super.get(name);
    }

    public String getNamespace() {
        return (String) this.nameSpace;
    }

    public String getPodname() {
        return (String) this.podName;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza;
    }

    public void setConfigFile(String configPath) {
        this.configPath = configPath;
    }

    public void generateLogs() {
        // do the log thing here.
        // make a rest call to the api.

        // poll the s3 endpoint for the log file

        // pull the log files.
    }

    public LoggingApiInstance newInstance(List<Object> objects) {
        LoggingApiInstance instance = new LoggingApiInstance(new Class("loggingapiinstance", null));
        return instance;
    }

}
