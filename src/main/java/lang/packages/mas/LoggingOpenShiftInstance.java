package lang.packages.mas;

import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.Interpreter;
import lang.Token;

import java.util.List;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

import io.fabric8.kubernetes.client.dsl.LogWatch;

public class LoggingOpenShiftInstance extends Instance {

    // public LoggingOpenShiftInstance loggingOpenShiftInstance;
    public String openshiftConfigPath;
    public String podName;
    public String nameSpace;

    public LoggingOpenShiftInstance(Class klass) {
        super(klass);
    }

    public Object get(Token name) {
        if (name.lexeme.equals("setOpenShiftConfig")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setOpenShiftConfig((String) arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
            // return loggingOpenShiftInstance;
        }
        if (name.lexeme.equals("setPodName")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setPodName((String) arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if (name.lexeme.equals("setNamespace")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        setNamespace((String) arguments.get(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
        }
        if (name.lexeme.equals("dumpToConsole")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        dumpToConsole();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    return null;
                }
            };
        }
        if (name.lexeme.equals("getPodName")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return getPodname();
                }
            };
        }
        if (name.lexeme.equals("getNamespace")) {
            return new Callable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return getNamespace();
                }
            };
        }
        return super.get(name);
    }

    public void dumpToConsole() throws Exception {
        try {
            // String kubeconfigPath = "";
            System.setProperty("kubeconfig", this.openshiftConfigPath);
            KubernetesClient client = new KubernetesClientBuilder().build();
            LogWatch ignore = client.pods()
                    .inNamespace(this.nameSpace)
                    .withName(this.podName)
                    .tailingLines(100)
                    .watchLog(System.out);
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String getNamespace() {
        return (String) this.nameSpace;
    }

    public String getPodname() {
        return (String) this.podName;
    }

    public void setNamespace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public void setOpenShiftConfig(String configPath) {
        this.openshiftConfigPath = configPath;
    }

    public MASLoggingInstance newInstance(List<Object> objects) {
        MASLoggingInstance instance = new MASLoggingInstance(new Class("maslogging", null));
        return instance;
    }

}
