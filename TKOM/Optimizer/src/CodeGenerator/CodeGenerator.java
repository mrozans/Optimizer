package CodeGenerator;

import Lexer.Token;
import Parser.Node;
import Parser.SyntaxTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeGenerator {
    private SyntaxTree syntaxTree;
    private String file;
    private Node currentNode;
    private int lastLevel;
    private int blocks;
    private int lock;

    public CodeGenerator(SyntaxTree syntaxTree) {
        this.syntaxTree = syntaxTree;
        file = "result.txt";
        currentNode = syntaxTree.getRoot();
        blocks = 0;
        lock = 0;
    }

    public void generateCode() throws FileNotFoundException {
        File result = new File(file);
        try {
            result.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.print(generateOutput());
        printWriter.close();
    }

    private String generateOutput(){
        StringBuilder result = new StringBuilder();
        syntaxTree.getRoot().calculateLevel(0);
        currentNode = currentNode.nextNode();
        currentNode = currentNode.nextNode();
        result.append("int main()\n");
        currentNode = currentNode.nextNode();
        result.append(generateBlock());
        return result.toString();
    }

    private String generateBlock(){
        StringBuilder result = new StringBuilder();
        result.append("\t".repeat(blocks)).append("{\n");
        blocks++;
        lastLevel = currentNode.getLevel();
        currentNode = currentNode.nextNode();
        while(currentNode != null && currentNode.getLevel() >= lastLevel){
            if(currentNode.getType() != null) {
                lastLevel = currentNode.getLevel();
                result.append("\t".repeat(blocks));
                result.append(generateAssign()).append("\n");
            }
            else {
                int tmp  = currentNode.getLevel();
                switch (currentNode.getTokenType()){
                    case Break:
                    case Continue:
                        result.append("\t".repeat(blocks)).append(currentNode.getTokenType().toString().toLowerCase()).append(";\n");
                        currentNode = currentNode.nextNode();
                        break;
                    case Int:
                    case Short:
                    case Long:
                    case Float:
                    case Double:
                    case Bool:
                        result.append("\t".repeat(blocks)).append(generateInitialization()).append("\n");
                        break;
                    case If:
                        result.append("\t".repeat(blocks)).append(generateIf());
                        break;
                    case For:
                        result.append("\t".repeat(blocks));
                        result.append(generateFor());
                        break;
                    case Return:
                        result.append("\t".repeat(blocks)).append("return ");
                        currentNode = currentNode.nextNode();
                        result.append(currentNode.getValue()).append(";\n");
                        currentNode = currentNode.nextNode();
                }
                lastLevel = tmp;
            }
        }
        blocks--;
        result.append("\t".repeat(blocks)).append("}\n");
        return result.toString();
    }

    private String generateInitialization(){
        StringBuilder result = new StringBuilder();
        result.append(currentNode.getTokenType().toString().toLowerCase());
        currentNode = currentNode.nextNode();
        while (currentNode.getType() != null && currentNode.getType().equals("variable initialization")){
            currentNode = currentNode.nextNode();
            result.append(" ").append(currentNode.getValue()).append(checkArray(false));
            if(currentNode.getTokenType() == Token.TokenType.Assign){
                result.append(" = ");
                currentNode = currentNode.nextNode();
                if(currentNode.getType().equals("group of values")){
                    result.append("{");
                    currentNode = currentNode.nextNode();
                    result.append(generateExpression());
                    while (currentNode.getType() != null && currentNode.getType().equals("expression")){
                        result.append(", ").append(generateExpression());
                    }
                    result.append("}");
                }
                else result.append(generateExpression());
            }
            if(currentNode.getType() != null && currentNode.getType().equals("variable initialization")) result.append(", ");
        }
        result.append(";");
        return result.toString();
    }

    private String generateFor(){
        StringBuilder result = new StringBuilder();
        result.append("for(");
        currentNode = currentNode.nextNode();
        if(currentNode.getType().equals("start statement")){
            currentNode = currentNode.nextNode();
            if(currentNode.getType() != null && currentNode.getType().equals("assign")) result.append(generateAssign());
            else result.append(generateInitialization());
        }
        else result.append("; ");
        if(currentNode.getType().equals("condition")){
            currentNode = currentNode.nextNode();
            result.append(generateExpression());
        }
        result.append("; ");
        if(currentNode.getType().equals("post statement")){
            currentNode = currentNode.nextNode();
            result.append(generateAssign());
            result.deleteCharAt(result.length()-1);
        }
        result.append(")\n").append(generateBlock());
        return result.toString();
    }

    private String generateAssign(){
        StringBuilder result = new StringBuilder();
        currentNode = currentNode.nextNode();
        result.append(currentNode.getValue()).append(checkArray(false));
        result.append(" = ");
        currentNode = currentNode.nextNode();
        result.append(generateExpression());
        result.append(";");
        return result.toString();
    }

    private String generateIf(){
        StringBuilder result = new StringBuilder();
        result.append("if(");
        currentNode = currentNode.nextNode();
        currentNode = currentNode.nextNode();
        result.append(generateExpression());
        result.append(") ");
        if(currentNode.getType() != null){
            if(currentNode.getType().equals("block")) result.append(generateBlock());
            else result.append(generateAssign()).append("\n");
        }
        else {
            result.append(currentNode.getTokenType().toString().toLowerCase()).append(";\n");
            currentNode = currentNode.nextNode();
        }
        return result.toString();
    }

    private String checkArray(boolean inExp){
        StringBuilder result = new StringBuilder();
        if(currentNode.getChildNodes().size() != 0) {
            currentNode = currentNode.nextNode();
            result.append("[").append(generateExpression()).append("]");
            if(inExp) lock++;
        }
        else if(!inExp) currentNode = currentNode.nextNode();
        return result.toString();
    }

    private String generateExpression(){
        StringBuilder result = new StringBuilder();
        currentNode = currentNode.nextNode();
        int level = currentNode.getLevel();
        while(currentNode.getLevel() >= level){
            if(currentNode.getType() != null && currentNode.getType().equals("expression")){
                int tmp = currentNode.getLevel();
                result.append("(").append(generateExpression()).append(")");
                level = tmp;
                continue;
            }
            else switch (currentNode.getTokenType()){
                case Identifier:
                    result.append(currentNode.getValue()).append(checkArray(true));
                    break;
                case Plus:
                    result.append(" + ");
                    break;
                case Minus:
                    result.append(" - ");
                    break;
                case Multiply:
                    result.append(" * ");
                    break;
                case Divide:
                    result.append(" / ");
                    break;
                case Modulo:
                    result.append(" % ");
                    break;
                case Or:
                    result.append(" || ");
                    break;
                case And:
                    result.append(" && ");
                    break;
                case Less:
                    result.append(" < ");
                    break;
                case LessOrEqual:
                    result.append(" <= ");
                    break;
                case Greater:
                    result.append(" > ");
                    break;
                case GreaterOrEqual:
                    result.append(" >= ");
                    break;
                case Equal:
                    result.append(" == ");
                    break;
                case NotEqual:
                    result.append(" != ");
                    break;
                case Negation:
                    result.append("!");
                case Number:
                case FiniteNumber:
                    result.append(currentNode.getValue());
                    break;
                case True:
                case False:
                    result.append(currentNode.getTokenType().toString().toLowerCase());

            }
            if(lock == 0) currentNode = currentNode.nextNode();
            else lock--;
        }
        return result.toString();
    }
}
