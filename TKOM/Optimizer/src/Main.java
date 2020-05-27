import CodeGenerator.CodeGenerator;
import Lexer.Lexer;
import Optimizer.Optimizer;
import Parser.Parser;
import SemanticAnalizer.SemanticAnalyzer;
import Parser.SyntaxTree;

import java.io.IOException;
import java.nio.file.Path;

public class Main
{
    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("\tNo file given\n");
            return;
        }
        Lexer lexer = new Lexer(Path.of(args[0]));
        if(!lexer.isValid()) {
            System.out.println("\tInvalid file\n");
            return;
        }
        Parser parser = new Parser(lexer);
        System.out.println("syntax analysis:\n");
        SyntaxTree syntaxTree = parser.parseProgram();
        syntaxTree.getRoot().calculateLevel(0);
        if(syntaxTree.isValid()) System.out.println("\tNo errors found\n");
        else return;
        System.out.println("semantic analysis:\n");
        SemanticAnalyzer semantic_analyzer = new SemanticAnalyzer(syntaxTree);
        semantic_analyzer.analise();
        if(semantic_analyzer.isValid()) System.out.println("\tNo errors found\n");
        else return;
        Optimizer optimizer = new Optimizer(syntaxTree);
        optimizer.optimize();
        CodeGenerator codeGenerator = new CodeGenerator(syntaxTree);
        codeGenerator.generateCode();
    }
}
