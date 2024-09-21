//package com.sms.lang;
//import static com.sms.lang.TokenType.*;
//import static com.sms.lang.Expr.*;
//import static com.sms.lang.Token.*;
package lang;
import static lang.TokenType.*;
import static lang.Expr.*;
import static lang.Token.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Parser {

    private static class ParseError extends RuntimeException {  }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while( ! end() ) {
            statements.add(declaration());
        }
        return statements;
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while(!check(RIGHT_BRACE) && ! end()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' after block");
        return statements;
    }

    private Stmt statement() {
        // add our embedded statement blocks here.
        if(match(DECORATOR)) return decoratorStatement();
        if(match(USE)) return useStatement();
        if(match(FOR)) return forStatement();
        if(match(IF)) return ifStatement();
        if(match(PRINT)) return printStatement();
        if(match(RETURN)) return returnStatement();
        if(match(WHILE)) return whileStatement();

        //if(match(HTTP)) return httpStatement();

        if(match(LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt decoratorStatement() {
        TokenType token = peek().type;
        switch(token) {
            case TEST:
                return testStatement();
            default:
                throw new ParseError();
        }
    }


    private Expr keyValueExpr() {
        Token token = previous();
        consume(LEFT_PAREN, "Expect '(' after the dollar. ");
        Expr key = expression();
        consume(COMMA, "Expect ',' after the key.");
        Expr value = expression();
        consume(RIGHT_PAREN, "Expect ')' after the value. ");
        return new Expr.Dollar(key, value);
    }

    private Stmt returnStatement() {
        Token keyword = previous(); // RETURN
        Expr value = null; // if we return nothing
        if(!check(SEMICOLON)) {
            value = expression(); // return [something]
        }
        consume(SEMICOLON, "Expect ';' after return value");
        return new Stmt.Return(keyword, value);
    }

    private Stmt useStatement() {
        Token keyword = previous(); // use
        Expr moduleName = expression(); // use "the package";
        consume(SEMICOLON, "Expect ';' after use package name");
        return new Stmt.Use(keyword, moduleName);
    }

    private Stmt forStatement() {

        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        // start with the opening of the for statment
        Stmt initializer;
        if(match(SEMICOLON)) {
            initializer = null;
        } else if(match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        // check if we have a condition
        Expr condition = null;
        if( ! check(SEMICOLON) ) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition");

        // check if we have the incrementer.
        Expr increment = null;
        if( !check( RIGHT_PAREN ) ) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses");

        Stmt body = statement();

        if(increment != null) {
            body = new Stmt.Block(
                Arrays.asList(
                    body,
                    new Stmt.Expression(increment)
                )
            );
        }

        // if we don't have a condition, we just infinitely loop here.
        if(condition == null) condition =  new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if(initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'");
        // this is the condition on which we loop.
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition");
        // this is the block
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }


    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if' ");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after 'if' condition");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if(match(ELSE)) { // here is where we build out the else conditional.
            elseBranch = statement(); // buildk
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after print statement.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name");
        Expr initializer = null;
        if(match(EQUAL)) {
            initializer = expression(); // get the expression on the right side of the =
        }
        consume(SEMICOLON, "Expect ';' after variable declaraction");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after experssion");
        return new Stmt.Expression(expr);
    }

    private Stmt testStatement() {
        // name of the function.
        consume(TEST, "Expect test after @.");
        Token token = previous();
        consume(FUN, "Expect a function after the test declaration");
        Stmt.Function func = function("testfunction");
        return new Stmt.Test(token, func);
    }

    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if(!check(RIGHT_PAREN)) {
            do {
                if(parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while(match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect a '{' before " + kind + " body");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    private Expr expression() {
        if(match(DOLLAR)) {
            return keyValueExpr();
        }
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();
        // Expr expr = equality();
        if(match(EQUAL)) { // 5 == 4

            Token equals = previous();
            Expr value = assignment();

            if(expr instanceof Expr.Variable) {

                Token name = ( (Expr.Variable) expr ).name;
                return new Expr.Assign(name, value);

            }
            else if(expr instanceof Expr.Get) {

                Expr.Get get = (Expr.Get)expr;
                return new Expr.Set(get.object, get.name, value);

            }

            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expr or() {
        Expr expr = and();
        while(match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        while(match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    /*
    Declaration -> Statement -> Expression -> (literal, unary, etc)
    */
    private Stmt declaration() {
        try {
            if(match(CLASS)) return classDeclaration();
            if(match(FUN)) return function("function");
            if(match(VAR)) return varDeclaration();
            return statement();
        } catch(ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt classDeclaration() {
        // class name
        Token name = consume(IDENTIFIER, "Expect a class name.");
        consume(LEFT_BRACE, "Expect '{' before class body.");
        // method names.
        List<Stmt.Function> methods = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !end()) {
            methods.add(function("method"));
        }
        consume(RIGHT_BRACE, "Expect '}' at end of class body");
        return new Stmt.Class(name, methods);
    }

    private Expr equality() {
        Expr expr = comparison();
        while(match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator  = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while(match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while(match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        // return primary();
        return call();
    }

    private Expr call() {

        Expr expr = primary();
        while(true) {
            if(match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        // NOTE
        if(!check(RIGHT_PAREN)) {
            do {
                if(arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments");
                }
                arguments.add(expression());
            } while(match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments");
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary() {

        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);

        if(match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if(match(THIS)) return new Expr.This(previous());

        if(match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        if(match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "expected expression");
    }


    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if(end()) return false;
        // check if the next token is the expected value of type.
        return peek().type == type;
    }

    private Token advance() {
        if(!end()) current++;
        return previous();
    }

    public boolean end() {
        return peek().type == EOF;
    }

    public Token peek() {
        return tokens.get(current);
    }

    public Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        System.out.println("Parse Error: " + token + " Message: " + message);
        throw new ParseError();
    }

    private void synchronize() {
        advance();
        while(!end()) {
            if(previous().type == SEMICOLON) return;
            switch(peek().type) {
                case CLASS: case FOR: case FUN : case IF: case PRINT: case RETURN: case VAR: case WHILE:
                    return;
            }
        }
    }

}
