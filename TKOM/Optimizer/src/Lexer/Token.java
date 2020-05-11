package Lexer;

public class Token {

    private TokenType type;
    private int line;
    private int position;
    private String value;

    Token(int line, int position) {
        this.line = line + 1;
        this.position = position;
    }

    void setType(TokenType type){
        this.type = type;
    }

    public TokenType getType(){
        return type;
    }

    void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getPosition() {
        return position;
    }

    public enum TokenType{
        Main, For, Break, Continue, Return, EOF,
        Int, Short, Long, Float, Double, Bool,
        Plus, Minus, Multiply, Divide, Modulo,
        If, Else, Or, And, Negation,
        Assign, Equal, NotEqual, Less, Greater, LessOrEqual, GreaterOrEqual,
        OpenBrace, ClosedBrace, OpenCurlyBrace, ClosedCurlyBrace, OpenSquareBrace, ClosedSquareBrace,
        Identifier, Comma, Semicolon,
        Number, FiniteNumber, True, False,
        Unknown
    }
}
