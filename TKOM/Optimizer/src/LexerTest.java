import Lexer.Lexer;
import Lexer.Token;

import java.io.IOException;
import java.nio.file.Path;

public class LexerTest {

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(Path.of(args[0]));
        Token token;
        do {
            token = lexer.nextToken();
            System.out.println(token.getType());
        }while (token.getType() != Token.TokenType.EOF);
    }
}
