
//package com.sms.lang;

package lang;
import java.util.*;


public class Token {
    final TokenType type;
    public final String lexeme;
    final Object literal;
    final int line;
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme=  lexeme;
        this.literal = literal ;
        this.line = line;
    }
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
