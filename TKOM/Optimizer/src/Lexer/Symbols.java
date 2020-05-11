package Lexer;

import java.util.HashMap;

class Symbols {
    static HashMap<String, Token.TokenType> keywords = new HashMap<>();
    static HashMap<Character, Token.TokenType> signs = new HashMap<>();
    Symbols(){
        keywords.put("main", Token.TokenType.Main);
        keywords.put("for", Token.TokenType.For);
        keywords.put("break", Token.TokenType.Break);
        keywords.put("continue", Token.TokenType.Continue);
        keywords.put("return", Token.TokenType.Return);
        keywords.put("int", Token.TokenType.Int);
        keywords.put("short", Token.TokenType.Short);
        keywords.put("long", Token.TokenType.Long);
        keywords.put("float", Token.TokenType.Float);
        keywords.put("double", Token.TokenType.Double);
        keywords.put("bool", Token.TokenType.Bool);
        keywords.put("if", Token.TokenType.If);
        keywords.put("else", Token.TokenType.Else);
        keywords.put("true", Token.TokenType.True);
        keywords.put("false", Token.TokenType.False);
        signs.put('(', Token.TokenType.OpenBrace);
        signs.put(')', Token.TokenType.ClosedBrace);
        signs.put('{', Token.TokenType.OpenCurlyBrace);
        signs.put('}', Token.TokenType.ClosedCurlyBrace);
        signs.put('[', Token.TokenType.OpenSquareBrace);
        signs.put(']', Token.TokenType.ClosedSquareBrace);
        signs.put('+', Token.TokenType.Plus);
        signs.put('-', Token.TokenType.Minus);
        signs.put('*', Token.TokenType.Multiply);
        signs.put('/', Token.TokenType.Divide);
        signs.put('%', Token.TokenType.Modulo);
        signs.put(';', Token.TokenType.Semicolon);
        signs.put(',', Token.TokenType.Comma);
    }
}
