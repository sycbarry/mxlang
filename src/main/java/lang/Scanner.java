
//package com.sms.lang;
//import static com.sms.lang.TokenType.*;
package lang;
import static lang.TokenType.*;

import java.util.*;

public class Scanner {

    public String raw;
    public int current = 0;
    public int start = 0;
    public int line = 1;
    public ArrayList<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("use", USE);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
        keywords.put("$", DOLLAR);
        keywords.put("@", DECORATOR);
        keywords.put("test", TEST);
    }

    public Scanner(String input) {
        this.raw = input;
    }

    public ArrayList<Token> chop() {
        while(!end()) {
            start = current;
            scan();
        }
        tokens.add(new Token(EOF, "", null, 1));
        return tokens;
    }

    public void scan() {
        char c = advance();
        switch(c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '$': addToken(DOLLAR); break;
            case '@': addToken(DECORATOR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if(match('/')) {
                    while(peek() != '\n' && ! end()) {
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if(isDigit(c)) {
                    number();
                }
                else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    System.out.println("unexpected character");
                }
                break;
        }
    }
    public void number() {
        while(isDigit(peek()))
            advance();
        if(peek() == '.' && isDigit(peekNext())) {
            advance();
            while(isDigit(peek()))
                advance();
        }
        addToken(NUMBER, Double.parseDouble(raw.substring(start, current)));
    }

    public void addDecorator() {
        while(peek() != '\n') advance();
        advance();
        String value = raw.substring(start, current);
        addToken(DECORATOR,value);
    }

    public char peekNext() {
        if(current + 1 >= raw.length() ) return '\0';
        return raw.charAt(current + 1);
    }

    public boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    public boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    public boolean isAlpha(char c) {
        return (c >= 'a' && c<= 'z') ||
               (c >= 'A' && c<= 'z') ||
               (c == '_');
    }

    public void string() {
        while(peek() != '"' && ! end()) {
            if(peek() == '\n') line++;
            advance();
        }
        if(end()) {
            // todo add error here.
        }
        advance();
        String value = raw.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
    public boolean match(char expected) {
        if(end() == true) return false;
        if(raw.charAt(current) != expected) return false;
        current++;
        return true;
    }
    public void identifier() {
        while(isAlphaNumeric(peek())) advance();
        String value = raw.substring(start, current);
        TokenType type = keywords.get(value);
        if( type == null ) type = IDENTIFIER;
        addToken(type);
    }

    public void addToken(TokenType token) {
        addToken(token, null);
    }
    public void addToken(TokenType type, Object literal) {
        String text = raw.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    public char peek() {
        if(end()) return '\0';
        return raw.charAt(current);
    }

    public char advance() {
        return raw.charAt(current++);
    }

    private boolean end() {
        return current >= raw.length();
    }

}
