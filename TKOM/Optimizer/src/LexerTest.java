import lexer.Lexer;
import lexer.Token;

import java.io.IOException;
import java.nio.file.Path;

public class LexerTest {

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(Path.of(args[0]));
        Token token;
        do {
            token = lexer.nextToken();
            System.out.println(token.type);
        }while (token.type != Token.TokenType.EOF);
    }
}
