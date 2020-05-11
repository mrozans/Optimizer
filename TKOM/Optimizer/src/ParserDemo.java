import Lexer.Lexer;
import Parser.Parser;
import Parser.SyntaxTree;

import java.io.IOException;
import java.nio.file.Path;

public class ParserDemo {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(Path.of(args[0]));
        Parser parser = new Parser(lexer);
        SyntaxTree syntaxTree = parser.parseProgram();
        System.out.println(syntaxTree.isValid());
        if(syntaxTree.isValid()) syntaxTree.printTree();
    }
}
