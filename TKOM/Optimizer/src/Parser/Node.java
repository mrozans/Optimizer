package Parser;

import Lexer.Token;

import java.util.ArrayList;

public class Node {
    private Token.TokenType tokenType;
    private String value;
    private ArrayList<Node> childNodes;
    private Node parentNode;
    private String type;
    private int level;
    private ArrayList<Integer> blocks;
    private ArrayList<Integer> forBlocks;
    private int line;

    Node() {
        tokenType = null;
        value = null;
        parentNode = null;
        childNodes = new ArrayList<Node>();
        type = null;
        blocks = new ArrayList<>();
        forBlocks = new ArrayList<>();
        line = 0;
    }

    Node(Node parentNode, Token token){
        this.parentNode = parentNode;
        childNodes = new ArrayList<Node>();
        parentNode.childNodes.add(this);
        tokenType = token.getType();
        value = token.getValue();
        blocks = new ArrayList<>();
        line = token.getLine();
    }

    public Node(Node parentNode, String type){
        this.parentNode = parentNode;
        childNodes = new ArrayList<Node>();
        if(parentNode != null) parentNode.childNodes.add(this);
        this.type = type;
        blocks = new ArrayList<>();
    }

    public Node(Node parentNode, Token.TokenType token, String value){
        this.parentNode = parentNode;
        childNodes = new ArrayList<Node>();
        if(parentNode != null) parentNode.childNodes.add(this);
        tokenType = token;
        this.value = value;
        blocks = new ArrayList<>();
    }

    Node newestNode(){
        if(childNodes.size() == 0) return this;
        return childNodes.get(childNodes.size()-1);
    }

    public Node nextNode(){
        if(childNodes.size() != 0) return childNodes.get(0);
        Node currentNode = this;
        while(true){
            Node tmp = currentNode;
            currentNode = currentNode.getParentNode();
            if(currentNode == null) return null;
            for(int i = 0; i < currentNode.childNodes.size() - 1; i++){
                if(currentNode.childNodes.get(i) == tmp) return currentNode.childNodes.get(i+1);
            }
        }
    }

    public void calculateLevel(int level){
        this.level = level;
        for (Node childNode : childNodes) {
            childNode.calculateLevel(level + 1);
        }
    }

    void printNode(){
        StringBuilder tmp = new StringBuilder();
        tmp.append("\t".repeat(level));
        if(type != null) tmp.append(type);
        else if(tokenType != null) tmp.append(tokenType);
        else tmp.append("root");
        if(value != null) tmp.append(": ").append(value);
        System.out.println(tmp);
        for (Node childNode : childNodes) {
            childNode.printNode();
        }
    }

    public ArrayList<Integer> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Integer> blocks) {
        this.blocks = blocks;
    }

    public String getType() {
        return type;
    }

    public Token.TokenType getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public int getLevel() {
        return level;
    }

    public int getLine() {
        return line;
    }

    public ArrayList<Integer> getForBlocks() {
        return forBlocks;
    }

    public void setForBlocks(ArrayList<Integer> forBlocks) {
        this.forBlocks = forBlocks;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public void addFakeChild(Node child){
        this.getChildNodes().add(child);
    }

    public void addNewChild(Node child){
        if(child.getParentNode() != null){
            int i = 0;
            for (Node childNode : child.getParentNode().getChildNodes()) {
                if(childNode == child) {
                    child.getParentNode().getChildNodes().remove(i);
                    break;
                }
                i++;
            }
        }
        this.getChildNodes().add(child);
        child.setParentNode(this);
    }
}
