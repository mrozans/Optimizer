import CodeGenerator.CodeGenerator;
import Lexer.Lexer;
import Parser.Parser;
import Parser.SyntaxTree;
import SemanticAnalizer.SemanticAnalyzer;

import java.io.IOException;
import java.nio.file.Path;

public class ParserDemo {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(Path.of(args[0]));
        Parser parser = new Parser(lexer);
        SyntaxTree syntaxTree = parser.parseProgram();
        syntaxTree.getRoot().calculateLevel(0);
        System.out.println(syntaxTree.isValid());
        if(syntaxTree.isValid()) syntaxTree.printTree();
        SemanticAnalyzer semantic_analyzer = new SemanticAnalyzer(syntaxTree);
        semantic_analyzer.analise();
        CodeGenerator codeGenerator = new CodeGenerator(syntaxTree);
        if(syntaxTree.isValid()) codeGenerator.generateCode();
    }
}
