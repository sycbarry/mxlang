//package com.sms.lang;
//import static com.sms.lang.Token.*;
package lang;
import static lang.Token.*;

import java.util.Arrays;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.Charset;

public class Lang {

    public final static Interpreter interpreter = new Interpreter();

    public static void main(String args[]) throws Exception {
        if(args.length <= 0) {
            throw new Exception("no file provided...");
        }
        runFile(args[0]);
    }

    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String raw = new String(bytes, Charset.defaultCharset());
        parse(raw);
    }

    public static void parse(String rawText) {
        Scanner scanner = new Scanner(rawText);
        ArrayList<Token> tokens = scanner.chop();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        interpreter.interpret(statements);
    }


}
