package Parser;

import Lexer.Token;

import java.util.ArrayList;

public class Node {
    private Token.TokenType tokenType;
    private String value;
    private ArrayList<Node> childNodes;
    private Node parentNode;
    private String type;
    private static int level = 0;

    Node() {
        tokenType = null;
        value = null;
        parentNode = null;
        childNodes = new ArrayList<Node>();
        type = null;
    }

    Node(Node parentNode, Token token){
        this.parentNode = parentNode;
        childNodes = new ArrayList<Node>();
        parentNode.childNodes.add(this);
        tokenType = token.getType();
        value = token.getValue();
    }

    Node(Node parentNode, String type){
        this.parentNode = parentNode;
        childNodes = new ArrayList<Node>();
        parentNode.childNodes.add(this);
        this.type = type;
    }

    Node nextNode(){
        if(childNodes.size() == 0) return this;
        return childNodes.get(childNodes.size()-1);
    }

    void printNode(){
        level++;
        StringBuilder tmp = new StringBuilder();
        tmp.append("\t".repeat(level - 1));
        if(type != null) tmp.append(type);
        else if(tokenType != null) tmp.append(tokenType);
        else tmp.append("root");
        if(value != null) tmp.append("- ").append(value);
        for (Node childNode : childNodes) {
            childNode.printNode();
        }
        level--;
    }

    public Token.TokenType getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    Node getParentNode() {
        return parentNode;
    }
}
