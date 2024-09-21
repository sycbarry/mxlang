
//package com.sms.lang;
package lang;


import java.util.List;
import java.util.HashMap;
import java.util.Map;

public abstract class Expr {

    interface Visitor<R> {
        R visitBinaryExpr (Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitVariableExpr(Variable expr);
        R visitLogicalExpr(Logical expr);
        R visitSetExpr(Set expr);
        R visitUnaryExpr(Unary expr);
        R visitThisExpr(This expr);
        R visitAssignExpr(Assign expr);
        R visitCallExpr(Call expr);
        R visitGetExpr(Get expr);
        R visitKeyValueExpr(Dollar expr);
    }

    public static class Dollar extends Expr {
        Dollar(Expr key, Expr value) {
            this.value = value;
            this.key = key;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitKeyValueExpr(this);
        }
        public final Expr key;
        public final Expr value;
    }



    static class This extends Expr {
        This(Token keyword) {
            this.keyword = keyword;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }
        final Token keyword;
    }

    static class Set extends Expr {
        Set(Expr object, Token name, Expr value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }
        final Expr object;
        final Token name;
        final Expr value;
    }

    static class Get extends Expr {
        Get(Expr object, Token name) {
            this.object = object;
            this.name = name;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }
        final Expr object;
        final Token name;
    }

    static class Call extends Expr {
        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
        final Expr callee;
        final Token paren;
        final List<Expr> arguments;
    }

    static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
        final Token name;
    }

    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
        final Expr left;
        final Expr right;
        final Token operator;
    }

    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right)  {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        final Expr left;
        final Token operator;
        final Expr right;
    }


    static class Assign extends Expr {
        Assign (Token name, Expr value) {
            this.name = name;
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
        final Token name;
        final Expr value;
    }


    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this) ;
        }
        final Expr expression;
    }

    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
        final Object value;
    }

    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }

    abstract <R> R accept(Visitor<R> visitor);

}
