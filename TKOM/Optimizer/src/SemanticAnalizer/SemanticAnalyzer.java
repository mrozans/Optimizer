package SemanticAnalizer;

import Lexer.Token;
import Parser.Node;
import Parser.SyntaxTree;

import java.util.ArrayList;

public class SemanticAnalyzer {
    private SyntaxTree syntaxTree;
    private int iterator;
    private ArrayList[] variables;
    private boolean valid;

    public SemanticAnalyzer(SyntaxTree syntaxTree) {
        this.syntaxTree = syntaxTree;
        iterator = 0;
        valid = true;
    }

    public void analise(){
        markNodes();
        variables = new ArrayList[iterator+1];
        for(int i = 0; i <= iterator; i++){
            variables[i] = new ArrayList<VariableInfo>();
        }
        checkInitialization();
        checkUsage();
        checkAssignment();
        checkArrayVariable();
    }

    private void markNodes(){
        NodeIterator nodeIterator = new NodeIterator(syntaxTree.getRoot());
        ArrayList<Integer> blockNumbers = new ArrayList<>();
        blockNumbers.add(0);
        while (nodeIterator.getCurrentNode() != null){
            if((nodeIterator.getCurrentNode().getTokenType() != null && nodeIterator.getCurrentNode().getTokenType() == Token.TokenType.For)
                    || NotForBlock(nodeIterator)){
                if(nodeIterator.notVisited()){
                    iterator++;
                    blockNumbers.add(iterator);
                }
                else if(nodeIterator.finished()){
                    blockNumbers.remove(blockNumbers.size()-1);
                }
            }
            if(nodeIterator.notVisited()) nodeIterator.getCurrentNode().setBlocks((ArrayList<Integer>) blockNumbers.clone());
            nodeIterator.step();
        }
    }

    private boolean NotForBlock(NodeIterator nodeIterator){
        return (nodeIterator.getCurrentNode().getType() != null && nodeIterator.getCurrentNode().getType().equals("block") &&
                !(nodeIterator.getCurrentNode().getParentNode().getTokenType() != null
                && nodeIterator.getCurrentNode().getParentNode().getTokenType() == Token.TokenType.For));
    }

    private void checkInitialization(){
        Node currentNode = syntaxTree.getRoot();
        while (currentNode != null){
            if(currentNode.getType() != null && currentNode.getType().equals("variable initialization")){
                currentNode = currentNode.nextNode();
                boolean error = present(currentNode, 0);
                if(!error) {
                    if(currentNode.getChildNodes().size() == 0)
                    variables[currentNode.getBlocks().get(currentNode.getBlocks().size()-1)].add(new VariableInfo(currentNode.getValue(), false));
                    else variables[currentNode.getBlocks().get(currentNode.getBlocks().size()-1)].add(new VariableInfo(currentNode.getValue(), true));
                }
                else semanticAnalyzerError(currentNode.getValue(), currentNode.getLine(), 0);
            }
            currentNode = currentNode.nextNode();
        }
    }

    private void checkUsage(){
        Node currentNode = syntaxTree.getRoot();
        while (currentNode != null) {
            if(currentNode.getTokenType() != null && currentNode.getTokenType().equals(Token.TokenType.Identifier)){
                if(!(currentNode.getParentNode().getType() != null && currentNode.getParentNode().getType().equals("variable initialization"))){
                    boolean initialized = present(currentNode, 1);
                    if(!initialized) semanticAnalyzerError(currentNode.getValue(), currentNode.getLine(), 1);
                }
                else{
                    ArrayList<VariableInfo> tmp = variables[currentNode.getBlocks().get(currentNode.getBlocks().size() - 1)];
                    for (VariableInfo variableInfo : tmp) {
                        if(variableInfo.getVariable().equals(currentNode.getValue())) variableInfo.setInitialized();
                    }
                }
            }
            currentNode = currentNode.nextNode();
        }
    }

    private void checkAssignment(){
        Node currentNode = syntaxTree.getRoot();
        while (currentNode != null) {
            if(currentNode.getTokenType() != null && currentNode.getTokenType().equals(Token.TokenType.Identifier)) checkArrayInitialization(currentNode);
            if(currentNode.getType() != null && currentNode.getType().equals("variable initialization")) checkValues(currentNode);
            currentNode = currentNode.nextNode();
        }
    }

    private boolean present(Node node, int mode){
        String name = node.getValue();
        ArrayList<Integer> blocks = node.getBlocks();
        boolean present = false;
        for (Integer block : blocks) {
            for(int i =0; i < variables[block].size(); i++) {
                VariableInfo tmp = (VariableInfo) variables[block].get(i);
                if (tmp.getVariable().equals(name)) {
                    if(mode == 0 || (mode == 1 && tmp.isInitialized())){
                            present = true;
                            break;
                    }
                }
                if (present) break;
            }
        }
        return present;
    }

    private void checkArrayInitialization(Node currentNode){
        Node tmp = currentNode;
        if(currentNode.getParentNode() != null && currentNode.getParentNode().getType() != null &&
                currentNode.getParentNode().getType().equals("variable initialization")
                && onlyChild(currentNode, "expression", null)){
            currentNode = currentNode.getChildNodes().get(0);
            if(onlyChild(currentNode, null, Token.TokenType.FiniteNumber))
                semanticAnalyzerError(tmp.getValue(), tmp.getLine(), 2);
            if(onlyChild(currentNode, null, Token.TokenType.Minus)){
                currentNode = currentNode.getChildNodes().get(0);
                if(onlyChild(currentNode, null, Token.TokenType.Number) || onlyChild(currentNode, null, Token.TokenType.FiniteNumber))
                    semanticAnalyzerError(tmp.getValue(), tmp.getLine(), 2);
            }
        }
    }

    private void checkValues(Node currentNode){
        if(currentNode.getChildNodes().size() == 3){
            Node tmp = currentNode.getChildNodes().get(2);
            if(tmp.getType().equals("expression")){
                    checkSimpleValue(currentNode, tmp);
                }
            else if(tmp.getType().equals("group of values")){
                for (Node childNode : tmp.getChildNodes()) {
                    checkSimpleValue(currentNode, childNode);
                }
            }
        }
    }

    private void checkSimpleValue(Node currentNode, Node tmp){
        if(onlyChild(tmp, null, Token.TokenType.Number) || onlyChild(tmp, null, Token.TokenType.FiniteNumber)){
            tmp = tmp.getChildNodes().get(0);
            if(currentNode.getParentNode().getTokenType() != Token.TokenType.Bool)
                if(!checkValue(tmp.getValue(), currentNode.getParentNode().getTokenType())){
                    currentNode = currentNode.getChildNodes().get(0);
                    semanticAnalyzerError(currentNode.getValue(), currentNode.getLine(), 3);
                }
        }
    }

    private boolean checkValue(String value, Token.TokenType tokenType){
        switch (tokenType){
            case Int:
                try {
                    Integer.parseInt(value);
                }
                catch (Exception e){
                    return false;
                }
                break;
            case Long:
                try {
                    Long.parseLong(value);
                }
                catch (Exception e){
                    return false;
                }
                break;
            case Short:
                try {
                    Short.parseShort(value);
                }
                catch (Exception e){
                    return false;
                }
                break;
            case Float:
                try {
                    Float.parseFloat(value);
                }
                catch (Exception e){
                    return false;
                }
                break;
            case Double:
                try {
                    Double.parseDouble(value);
                }
                catch (Exception e){
                    return false;
                }
                break;
        }
        return true;
    }

    private boolean onlyChild(Node currentNode, String type,  Token.TokenType tokenType){
        if(currentNode.getChildNodes() != null && currentNode.getChildNodes().size() == 1){
            if(type != null){
                return currentNode.getChildNodes().get(0).getType().equals(type);
            }
            else return currentNode.getChildNodes().get(0).getTokenType() == tokenType;
        }
        return false;
    }

    private void checkArrayVariable(){
        Node currentNode = syntaxTree.getRoot();
        while (currentNode != null) {
            if(currentNode.getTokenType() != null && currentNode.getTokenType().equals(Token.TokenType.Identifier))
            {
                if(currentNode.getChildNodes().size() != 0) {
                    lookFor(0, currentNode);
                    if(currentNode.getChildNodes().get(0).getChildNodes().get(0).getTokenType() != null &&
                            currentNode.getChildNodes().get(0).getChildNodes().get(0).getTokenType() == Token.TokenType.FiniteNumber &&
                            currentNode.getParentNode().getType() != null &&
                            !currentNode.getParentNode().getType().equals("variable initialization"))
                        semanticAnalyzerError(currentNode.getValue(), currentNode.getLine(), 5);
                }
                else lookFor(1, currentNode);
            }
            currentNode = currentNode.nextNode();
        }
    }

    private void lookFor(int mode, Node currentNode){
        for (ArrayList<VariableInfo> tmp : variables) {
            for (VariableInfo variableInfo : tmp) {
                if(variableInfo.getVariable().equals(currentNode.getValue())){
                    if(mode == 0 && !variableInfo.isArray()) semanticAnalyzerError(currentNode.getValue(), currentNode.getLine(), 4);
                    else if(mode == 1 && variableInfo.isArray()) semanticAnalyzerError(currentNode.getValue(), currentNode.getLine(), 6);
                    return;
                }
            }
        }
    }

    private void semanticAnalyzerError(String variable, int line, int mode){
        String tmp = "Error line: " + line + " Variable: " + variable;
        if(mode == 0) tmp+= " initialized more than once\n";
        else if(mode == 1) tmp+= " used without initialization\n";
        else if(mode == 2) tmp+= " expected natural number in array initialization\n";
        else if(mode == 3) tmp+= " value assigned does not match given type\n";
        else if(mode == 4) tmp+= " variable is not an array\n";
        else if(mode == 5) tmp+= " expected integer in array\n";
        else if(mode == 6) tmp+= " variable is an array, index required\n";
        System.out.println(tmp);
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }
}