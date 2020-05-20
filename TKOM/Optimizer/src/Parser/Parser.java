package Parser;

import Lexer.Lexer;
import Lexer.Token;

import java.util.ArrayList;

import static Lexer.Token.TokenType.*;

public class Parser {

    private Lexer lexer;
    private SyntaxTree syntaxTree;
    private Node currentNode;
    private Token bufferedToken;
    private int braceCount;
    private int inFor;
    private boolean inForBrace;
    private int inArray;
    private boolean inIf;
    private boolean minusLast;
    private boolean plusLast;
    private boolean lastArray;
    private boolean comeBack;
    private ArrayList<Boolean> signBrace;
    private ArrayList<Boolean> negationBrace;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        syntaxTree = new SyntaxTree();
        currentNode = syntaxTree.getRoot();
        bufferedToken = null;
        braceCount = 0;
        inFor = 0;
        inArray = 0;
        inForBrace = false;
        inIf = false;
        minusLast = false;
        plusLast = false;
        lastArray = false;
        comeBack = true;
        signBrace = new ArrayList<>();
        negationBrace = new ArrayList<>();
    }

    public SyntaxTree parseProgram(){
        accept(lexer.nextToken(), Int);
        currentNode = currentNode.newestNode();
        accept(lexer.nextToken(), Main);
        currentNode = currentNode.getParentNode();
        accept(lexer.nextToken(), OpenBrace);
        accept(lexer.nextToken(), ClosedBrace);
        accept(lexer.nextToken(), OpenCurlyBrace);
        parseMainBlock();
        accept(lexer.nextToken(), ClosedCurlyBrace);
        return syntaxTree;
    }

    private void parseMainBlock(){
        bufferedToken = lexer.nextToken();
        currentNode = new Node(currentNode, "main block");
        while(syntaxTree.isValid() && bufferedToken.getType() != Return){
            switch (bufferedToken.getType()) {
                case For:
                    parseFor();
                    break;
                case If:
                    parseIf();
                    break;
                case Int:
                case Short:
                case Long:
                case Float:
                case Double:
                case Bool:
                    parseInit();
                    break;
                case Identifier:
                    parseIdentifier();
                    break;
                default:
                    parserError(bufferedToken, " start of statement", null, 1);
                    break;
            }
            if(bufferedToken == null) bufferedToken = lexer.nextToken();
        }
        if(bufferedToken.getType() == EOF) return;
        addToTree(bufferedToken);
        currentNode = currentNode.newestNode();
        accept(lexer.nextToken(), Number);
        accept(lexer.nextToken(), Semicolon);
    }

    private void parseBlock(){
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        accept(bufferedToken, OpenCurlyBrace);
        Node parent = currentNode;
        currentNode = new Node(currentNode, "block");
        bufferedToken = lexer.nextToken();
        while(syntaxTree.isValid() && bufferedToken.getType() != ClosedCurlyBrace){
            switch (bufferedToken.getType()) {
                case For:
                    parseFor();
                    break;
                case If:
                    parseIf();
                    break;
                case Int:
                case Short:
                case Long:
                case Float:
                case Double:
                case Bool:
                    parseInit();
                    break;
                case Identifier:
                    parseIdentifier();
                    break;
                case Continue:
                case Break:
                    parseLoopExpression();
                    break;
                default:
                    parserError(bufferedToken, " start of statement", null, 1);
                    break;
            }
            if(bufferedToken == null) bufferedToken = lexer.nextToken();
        }
        accept(bufferedToken, ClosedCurlyBrace);
        currentNode = parent;
    }

    private void parseFor(){
        inFor++;
        addToTree(bufferedToken);
        Node parent = currentNode;
        currentNode = currentNode.newestNode();
        accept(lexer.nextToken(), OpenBrace);
        inForBrace = true;
        parseForBrace();
        inForBrace = false;
        parseBlock();
        currentNode = parent;
        inFor--;
    }

    private void parseForBrace(){
        bufferedToken = lexer.nextToken();
        parseForStartStatement();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        parseForCondition();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        parseForPostStatement();
    }

    private void parseForStartStatement(){
        switch (bufferedToken.getType()){
            case Semicolon:
                addToTree(bufferedToken);
                break;
            case Int:
            case Short:
            case Long:
            case Float:
            case Double:
            case Bool:
                currentNode = new Node(currentNode, "start statement");
                parseInit();
                currentNode = currentNode.getParentNode();
                break;
            default:
                currentNode = new Node(currentNode, "start statement");
                parseAssign();
                if(bufferedToken == null) bufferedToken = lexer.nextToken();
                accept(bufferedToken, Semicolon);
                currentNode = currentNode.getParentNode();
                break;
        }
    }

    private void parseForCondition(){
        if(bufferedToken.getType().equals(Semicolon)) addToTree(bufferedToken);
        else{
            currentNode = new Node(currentNode, "condition");
            parseMainExpression();
            if(bufferedToken == null) bufferedToken = lexer.nextToken();
            accept(bufferedToken, Semicolon);
            currentNode = currentNode.getParentNode();
        }
    }

    private void parseForPostStatement(){
        if(bufferedToken.getType().equals(ClosedBrace)) addToTree(bufferedToken);
        else {
            currentNode = new Node(currentNode, "post statement");
            parseAssign();
            if(bufferedToken == null) bufferedToken = lexer.nextToken();
            accept(bufferedToken, ClosedBrace);
            currentNode = currentNode.getParentNode();
        }
    }

    private void parseIf(){
        addToTree(bufferedToken);
        Node parent = currentNode;
        currentNode = currentNode.newestNode();
        inIf = true;
        parseMainExpression();
        inIf = false;
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        if(bufferedToken.getType().equals(OpenCurlyBrace)) parseBlock();
        else parseIfExpression();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        if(bufferedToken.getType().equals(Else)) {
            addToTree(bufferedToken);
            currentNode = currentNode.newestNode();
            if(bufferedToken.getType().equals(OpenCurlyBrace)) parseBlock();
            else parseIfExpression();
        }
        currentNode = parent;
    }

    private void parseInit(){
        addToTree(bufferedToken);
        Node parent = currentNode;
        currentNode = currentNode.newestNode();
        currentNode = new Node(currentNode, "variable initialization");
        parseVariable();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        if(!bufferedToken.getType().equals(Semicolon) && !bufferedToken.getType().equals(Comma)){
            accept(bufferedToken, Assign);
            bufferedToken = lexer.nextToken();
            if(bufferedToken.getType().equals(OpenCurlyBrace)){
                addToTree(bufferedToken);
                currentNode = new Node(currentNode, "group of values");
                while(syntaxTree.isValid()){
                    parseMainExpression();
                    if(bufferedToken == null) bufferedToken = lexer.nextToken();
                    if(bufferedToken.getType().equals(ClosedCurlyBrace)){
                        addToTree(bufferedToken);
                        break;
                    }
                    else accept(bufferedToken, Comma);
                }
            }
            else if(lastArray) parserError(bufferedToken, "group of values", null, 1);
            else parseMainExpression();
        }
        currentNode = parent;
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        if(bufferedToken.getType().equals(Comma)) parseInit();
        else accept(bufferedToken, Semicolon);
        currentNode = parent;
    }

    private void parseAssign(){
        new Node(currentNode, "assign");
        Node parent = currentNode;
        currentNode = currentNode.newestNode();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        parseVariable();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        accept(bufferedToken, Assign);
        parseMainExpression();
        currentNode = parent;
    }

    private void parseIdentifier(){
        parseAssign();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        accept(bufferedToken, Semicolon);
    }

    private void parseMainExpression(){
        parseExpression();
        if(braceCount != 0 && !(inForBrace && braceCount == -1)) parserError(bufferedToken, null, null, 3);
    }

    private void parseExpression(){
        comeBack = false;
        new Node(currentNode, "expression");
        Node parent = currentNode;
        currentNode = currentNode.newestNode();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        parseExpressionValue();
        if(!comeBack) currentNode = parent.newestNode();
        boolean end = false;
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        while(syntaxTree.isValid() && !(inIf && braceCount == 0) && !(inForBrace && braceCount == -1)
                && !(bufferedToken.getType() == ClosedSquareBrace && inArray != 0)) {
            switch (bufferedToken.getType()){
                case Plus:
                    plusLast = true;
                    addToTree(bufferedToken);
                    break;
                case Minus:
                    minusLast = true;
                    addToTree(bufferedToken);
                    break;
                case Multiply:
                case Divide:
                case Or:
                case And:
                case Less:
                case LessOrEqual:
                case Greater:
                case GreaterOrEqual:
                case Equal:
                case NotEqual:
                    addToTree(bufferedToken);
                    break;
                case Break:
                case Continue:
                case Semicolon:
                case ClosedBrace:
                case Comma:
                case ClosedCurlyBrace:
                    end = true;
                    break;
                default:
                    parserError(bufferedToken, " operator", null, 1);
                    break;
            }
            if (!syntaxTree.isValid() || end) break;
            parseExpressionValue();
            if(bufferedToken == null) bufferedToken = lexer.nextToken();
        }
        if(!(signBrace.size() > 0 && (signBrace.get(signBrace.size() - 1) || negationBrace.get(negationBrace.size() - 1)))) currentNode = parent;
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        if(braceCount < 0 && !(inForBrace && braceCount == -1)) parserError(bufferedToken, null, null, 3);
        if(bufferedToken != null && !inForBrace && bufferedToken.getType() == ClosedBrace)  {
            currentNode = currentNode.getParentNode();
            addToTree(bufferedToken);
            checkBrace();
            braceCount--;
        }
    }

    private void parseIfExpression(){
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        switch (bufferedToken.getType()) {
            case Identifier:
                parseAssign();
                if(bufferedToken == null) bufferedToken = lexer.nextToken();
                accept(bufferedToken, Semicolon);
                break;
            case Continue:
            case Break:
                parseLoopExpression();
                break;
            default:
                parserError(bufferedToken, " assign statement or instruction", null, 1);
                break;
        }
    }

    private void parseExpressionValue() {
        boolean neg = false;
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        if(bufferedToken.getType().equals(Negation)){
            plusLast = false;
            minusLast = false;
            addToTree(bufferedToken);
            currentNode = currentNode.newestNode();
            bufferedToken = lexer.nextToken();
            neg = true;
        }
        if(bufferedToken.getType().equals(OpenBrace)){
            if(neg) negationBrace.add(true);
            else negationBrace.add(false);
            signBrace.add(false);
            plusLast = false;
            minusLast = false;
            braceCount++;
            addToTree(bufferedToken);
            parseExpression();
        }
        else parseValue();
        if(neg) currentNode = currentNode.getParentNode();
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        if(bufferedToken.getType().equals(ClosedBrace) && !(braceCount == 0 && inForBrace)){
            currentNode = currentNode.getParentNode();
            braceCount--;
            addToTree(bufferedToken);
            checkBrace();
            comeBack = true;
        }
    }

    private void parseVariable(){
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        accept(bufferedToken, Identifier);
        Node parent = currentNode;
        currentNode = currentNode.newestNode();
        bufferedToken = lexer.nextToken();
        if(bufferedToken.getType().equals(OpenSquareBrace)){
            lastArray = true;
            addToTree(bufferedToken);
            inArray++;
            parseExpression();
            inArray--;
            bufferedToken = null;
        }
        else lastArray = false;
        currentNode = parent;
    }

    private void parseValue(){
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        Node parent = currentNode;
        if((bufferedToken.getType().equals(Plus) && plusLast) || (bufferedToken.getType().equals(Minus) && minusLast)) {
            parserError(bufferedToken, " finite number or name of variable", null, 1);
        }
        else if(bufferedToken.getType().equals(Plus) || bufferedToken.getType().equals(Minus)){
            addToTree(bufferedToken);
            currentNode = currentNode.newestNode();
            bufferedToken = lexer.nextToken();
            if(bufferedToken.getType().equals(OpenBrace)){
                signBrace.add(true);
                negationBrace.add(false);
                addToTree(bufferedToken);
                braceCount++;
                parseExpression();
                return;
            }
        }
        if(bufferedToken == null) bufferedToken = lexer.nextToken();
        plusLast = false;
        minusLast = false;
        switch (bufferedToken.getType()){
            case Number:
            case FiniteNumber:
            case True:
            case False:
                addToTree(bufferedToken);
                break;
            case Identifier:
                parseVariable();
                break;
            default:
                parserError(bufferedToken, " finite number or name of variable", null, 1);
        }
        currentNode = parent;
    }

    private void parseLoopExpression(){
        if(inFor <= 0) parserError(bufferedToken, null, null, 2);
        else
        {
            addToTree(bufferedToken);
            accept(lexer.nextToken(), Semicolon);
        }
    }

    private void addToTree(Token nextToken){
        bufferedToken = null;
        if(!syntaxTree.isValid()) return;
        switch (nextToken.getType()){
            case ClosedBrace:
            case ClosedCurlyBrace:
            case ClosedSquareBrace:
            case OpenBrace:
            case OpenCurlyBrace:
            case OpenSquareBrace:
            case Semicolon:
            case Comma:
                return;
            default:
                new Node(currentNode, nextToken);
                break;
        }
    }

    private void checkBrace(){
        if(signBrace.size() <= 0) return;
        if(signBrace.get(signBrace.size() - 1) || negationBrace.get(negationBrace.size() - 1)) currentNode = currentNode.getParentNode();
        negationBrace.remove(negationBrace.size()-1);
        signBrace.remove(signBrace.size() - 1);
    }

    private void accept(Token nextToken, Token.TokenType expectedToken) {
        bufferedToken = null;
        if(nextToken.getType().equals(expectedToken)) {
            if(syntaxTree.isValid()) addToTree(nextToken);
        }
        else if(syntaxTree.isValid()) parserError(nextToken, null, expectedToken, 0);
    }

    private void parserError(Token token, String type, Token.TokenType expectedToken, int mode){
        if(!syntaxTree.isValid()) return;
        syntaxTree.setValid(false);
        String tmp = "First detected error at line: " + token.getLine();
        if(mode == 3) {
            tmp+= " Wrong number or placement of braces in expression"  + "\n";
        }
        else{
            tmp += " position: " + token.getPosition();
            if(mode == 2) tmp+= " use of: " + token.getType() + " forbidden outside of loop\n";
            else if(mode == 1) tmp+= " got: " + token.getType() + " expected: " + type + "\n";
            else tmp+= " got: " + token.getType() + " expected: " + expectedToken + "\n";
        }
        System.out.println(tmp);
        if(mode != 3)bufferedToken = null;
    }
}
