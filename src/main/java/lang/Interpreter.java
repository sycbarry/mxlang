//package com.sms.lang;
//import static com.sms.lang.TokenType.*;
//import com.sms.lang.modules.*;
//import com.sms.lang.packages.utils.*;
//import com.sms.lang.packages.mxtest.*;
//import src.main.java.packages.report.*;
package lang;

import lang.modules.*;
import lang.packages.utils.*;
import lang.packages.mxtest.*;
// import lang.packages.mas.*;
import lang.packages.report.*;

import java.util.List;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    final Environment globals = new Environment();
    private Environment environment = globals;// new Environment();
    private final Map<Expr, Integer> locals = new HashMap<>();
    private final Map<String, Integer> seenUseStatements = new HashMap<>();
    public List<Stmt> interpreterStatements;
    public List<Stmt> useStatements;
    public List<Stmt> coreStatements;
    private int position = 0;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Interpreter() {

        // here we make new callable functions that include base interface methods.
        // we can define more of these here as implicit functions/methods we can call
        // natively in our script.
        globals.define("clock", new Callable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });

        globals.define("int", new Callable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Integer call(Interpreter interpreter, List<Object> arguments) {
                Double newData = (Double) arguments.get(0);
                return newData.intValue();
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });

        globals.define("typeof", new Callable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return arguments.get(0).getClass();
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });

        globals.define("length", new Callable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Double call(Interpreter interpreter, List<Object> arguments) {
                if (arguments.get(0) instanceof String) {
                    String value = (String) arguments.get(0);
                    return (Double) (double) value.length();
                } else {
                    throw new RuntimeException("Must be of type String/string" + arguments.get(0));
                }
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });

    }

    public void interpret(List<Stmt> interpreterStatements) {
        this.interpreterStatements = interpreterStatements;
        try {
            // perform use statement parse.
            useStatements = new ArrayList<>();
            coreStatements = new ArrayList<>();
            for (Stmt stmts : interpreterStatements) {
                if (stmts instanceof Stmt.Use) {
                    useStatements.add(stmts);
                } else {
                    coreStatements.add(stmts);
                }
            }
            for (Stmt stmt : useStatements) {
                execute(stmt);
            }
            // execute the core program after having added our
            // modules
            for (Stmt stmt : coreStatements) {
                execute(stmt);
            }
        } catch (RuntimeError error) {
            System.out.println("error during interpreting statement: " + error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private void addPackageToInterpreter(String fileName) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            String raw = new String(bytes, Charset.defaultCharset());
            Scanner scanner = new Scanner(raw);
            ArrayList<Token> t = scanner.chop();
            Parser p = new Parser(t);
            List<Stmt> statements = p.parse();
            coreStatements.addAll(0, statements);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // on "use" statement.
    @Override
    public Void visitUseStmt(Stmt.Use stmt) {
        String packageName = stringify(evaluate(stmt.value));
        if (seenUseStatements.containsKey(packageName))
            return null;
        seenUseStatements.put(packageName, 1);
        switch (packageName) {
            // MXLang Modules
            case "test":
                addPackageToInterpreter("lang/modules/test.mx");
                break;
            // Core Instance Library
            case "report":
                globals.define("report", new ReportInstance(new Class("report", null)));
                break;
            case "mas":
                // TODO add MASInstance when completed. Some bugs around OCP .jar files included
                // here.
                // globals.define("mas", new MASInstance(new Class("mas", null)));
                break;
            case "mxtest":
                globals.define("mxtest", new MXTestInstance(new Class("mxtest", null)));
                break;
            case "utils": // add more utils here.
                globals.define("array", new ArrayInstance(new Class("array", null)));
                globals.define("dictionary", new DictionaryInstance(new Class("dictionary", null)));
                break;
            case "http":
                globals.define("http", new HttpInstance(new Class("http", null)));
                break;
            default:
                break;
        }
        // add package definitions here.
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        Function function = new Function(stmt, environment, false); // capture the current environment.
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch); // execute the then branch
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof Callable)) { // this checks weird semantics like: "this is function"();
            // it checks to validate that we are actually performing the operation on a
            // Callable type.
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }
        Callable function = (Callable) callee;
        // this is where we check for arity in our function blocks.
        if (arguments.size() != function.arity() && function.arity() != -1) { // arity is the amount of arguments the
                                                                              // function expects in its declaration.
            throw new RuntimeError(expr.paren,
                    "Expected" + function.arity() + " arguments but got" + arguments.size() + " .");
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof Instance) {
            return ((Instance) object).get(expr.name);
        }
        throw new RuntimeException(" only instances have properties.");
    }

    @Override
    public Void visitTestStmt(Stmt.Test stmt) {
        environment.define("failNow", new Callable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Void call(Interpreter interpreter, List<Object> arguments) {
                String error = (String) arguments.get(0);
                System.out.println(ANSI_RED + "FAIL " + ANSI_RESET + "-- test @ <" + stmt.function.name.lexeme + ">: "
                        + error + ANSI_RESET);
                throw new RuntimeException("");
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
        environment.define("fail", new Callable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Void call(Interpreter interpreter, List<Object> arguments) {
                String error = (String) arguments.get(0);
                System.out.println(ANSI_RED + "FAIL " + ANSI_RESET + "-- test @ <" + stmt.function.name.lexeme + ">: "
                        + error + ANSI_RESET);
                return null;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
        environment.define("pass", new Callable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Void call(Interpreter interpreter, List<Object> arguments) {
                System.out.println(ANSI_GREEN + "PASS " + ANSI_RESET + "-- test @ <" + stmt.function.name.lexeme + ">");
                return null;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
        environment.define("assertEquals", new Callable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Void call(Interpreter interpreter, List<Object> arguments) {
                Object left = arguments.get(0);
                Object right = arguments.get(1);
                if (!(left.getClass().equals(right.getClass()))) {
                    String error = left.toString() + " is not the same type as " + right.toString();
                    System.out.println(ANSI_RED + "FAIL " + ANSI_RESET + "-- test @ <" + stmt.function.name.lexeme
                            + ">: " + error + ANSI_RESET);
                    throw new RuntimeException("");
                }
                if (left instanceof String) {
                    if (((String) left).indexOf((String) right) == -1) {
                        String error = left.toString() + " is not the same as " + right.toString();
                        System.out.println(ANSI_RED + "FAIL " + ANSI_RESET + "-- test @ <" + stmt.function.name.lexeme
                                + ">: " + error + ANSI_RESET);
                        throw new RuntimeException("");
                    }
                }
                if (left instanceof Double && right instanceof Double) {
                    if ((Double) left != (Double) right) {
                        String error = left.toString() + " is not the same as " + right.toString();
                        System.out.println(ANSI_RED + "FAIL " + ANSI_RESET + "-- test @ <" + stmt.function.name.lexeme
                                + ">: " + error + ANSI_RESET);
                        throw new RuntimeException("");
                    }
                }

                System.out.println(ANSI_GREEN + "PASS " + ANSI_RESET + "-- test @ <" + stmt.function.name.lexeme
                        + ">: assertEquals ");
                return null;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
        Function function = new Function(stmt.function, environment, false); // capture the current environment.
        environment.define(stmt.function.name.lexeme, stmt.function);
        function.call(this, null);
        return null;
    }

    @Override
    public Object visitKeyValueExpr(Expr.Dollar expr) {
        Object value = evaluate(expr.value);
        Object key = evaluate(expr.key);
        Map<Object, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value); // add a value to the env.
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        // environment.assign(expr.name, value);
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left))
                return left;
        } else {
            if (!isTruthy(left))
                return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof Instance)) {
            System.out.println(object);
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }
        Object value = evaluate(expr.value);
        ((Instance) object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null)
            value = evaluate(stmt.value);
        throw new Return(value);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        environment.define(stmt.name.lexeme, null);
        Map<String, Function> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            Function function = new Function(method, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }
        Class klass = new Class(stmt.name.lexeme, methods);
        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        Object obj = lookUpVariable(expr.name, expr);
        return obj;
        // return environment.get(expr.name);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
        }
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperand(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperand(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperand(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperand(expr.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case MINUS:
                checkNumberOperand(expr.operator, left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator, "operands must be two numbers of 2 strings");
            case SLASH:
                checkNumberOperand(expr.operator, left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperand(expr.operator, left, right);
                return (double) left * (double) right;
        }
        return null;
    }

    public void executeBlock(List<Stmt> statements, Environment environment) {
        // the interpreter's environment temporarily becomes the block's environment.
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new RuntimeError(operator, "operand must be a number");
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;
        return true;
    }

    public void checkNumberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double)
            return;
        throw new RuntimeError(operator, "operands must be numbers");
    }

    public boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null)
            return "nil";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

}
