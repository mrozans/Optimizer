package lexer;

import FileHandler.FileHandler;
import java.io.IOException;
import java.nio.file.Path;

public class Lexer {

    private FileHandler fileHandler;

    public Lexer(Path path) throws IOException {
        new Symbols();
        fileHandler = new FileHandler(path);
    }
    public Token nextToken()
    {
        char sign = fileHandler.nextSign();
        while(sign == '\t' || sign == ' ') sign = fileHandler.nextSign();
        Token token = new Token(fileHandler.getLineNumber(), fileHandler.getSignPos());
        if(Character.isLetter(sign)){
            StringBuilder buffer = new StringBuilder();
            do{
                buffer.append(sign);
                fileHandler.skip();
                sign = fileHandler.peak();
            }while(Character.isDigit(sign) || Character.isLetter(sign));
            token.setType(Symbols.keywords.get(buffer.toString()));
            if(token.getType() == null) {
                token.setType(Token.TokenType.Identifier);
                token.setValue(buffer.toString());
            }
        }
        else if(Character.isDigit(sign)){
            StringBuilder buffer = new StringBuilder();
            do{
                buffer.append(sign);
                fileHandler.skip();
                sign = fileHandler.peak();
            }while(Character.isDigit(sign));
            if(sign != '.') {
                token.setType(Token.TokenType.Number);
            }
            else{
                do{
                    buffer.append(sign);
                    fileHandler.skip();
                    sign = fileHandler.peak();
                }while(Character.isDigit(sign));
                token.setType(Token.TokenType.FiniteNumber);
            }
            token.setValue(buffer.toString());
        }
        else if(sign  == 0){
            token.setType(Token.TokenType.EOF);
        }
        else if(sign == '!'){
            sign = fileHandler.peak();
            if(sign == '=') {
                token.setType(Token.TokenType.NotEqual);
                fileHandler.skip();
            }
            else token.setType(Token.TokenType.Negation);
        }
        else if(sign == '='){
            sign = fileHandler.peak();
            if(sign == '=') {
                token.setType(Token.TokenType.Equal);
                fileHandler.skip();
            }
            else token.setType(Token.TokenType.Assign);
        }
        else if(sign == '<'){
            sign = fileHandler.peak();
            if(sign == '=') {
                token.setType(Token.TokenType.LessOrEqual);
                fileHandler.skip();
            }
            else token.setType(Token.TokenType.Less);
        }
        else if(sign == '>'){
            sign = fileHandler.peak();
            if(sign == '=') {
                token.setType(Token.TokenType.GreaterOrEqual);
                fileHandler.skip();
            }
            else token.setType(Token.TokenType.Greater);
        }
        else if(sign == '|'){
            sign = fileHandler.peak();
            if(sign == '=') {
                token.setType(Token.TokenType.Or);
                fileHandler.skip();
            }
            else token.setType(Token.TokenType.Unknown);
        }
        else if(sign == '&'){
            sign = fileHandler.peak();
            if(sign == '=') {
                token.setType(Token.TokenType.And);
                fileHandler.skip();
            }
            else token.setType(Token.TokenType.Unknown);
        }
        else {

            token.setType(Symbols.signs.get(sign));
            if(token.getType() == null) token.setType(Token.TokenType.Unknown);
        }
        return token;
    }
}
