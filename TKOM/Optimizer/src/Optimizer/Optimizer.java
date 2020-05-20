package Optimizer;

import Lexer.Token;
import Parser.Node;
import Parser.SyntaxTree;

import java.util.ArrayList;
import java.util.HashMap;

public class Optimizer {
    private SyntaxTree syntaxTree;
    private int generated;
    private int forCounter;
    private HashMap<String, ValueInfo> valueInfos;
    private ArrayList<String> generatedNames;

    public Optimizer(SyntaxTree syntaxTree) {
        this.syntaxTree = syntaxTree;
        generated = 0;
        forCounter = 0;
        valueInfos = new HashMap<>();
        generatedNames = new ArrayList<>();
    }

    public void optimize(){
        mark();
        lookForExpressions();
        fixSigns();
    }

    private void mark(){
        ArrayList<Integer> forList = new ArrayList<>();
        ArrayList<Integer> forNodeList = new ArrayList<>();
        Node currentNode = syntaxTree.getRoot();
        while(currentNode != null){
            while(forNodeList.size() != 0 && currentNode.getLevel() <= forNodeList.get(forNodeList.size()-1)){
                forList.remove(forList.size()-1);
                forNodeList.remove(forNodeList.size()-1);
            }
            if(currentNode.getTokenType() != null && currentNode.getTokenType() == Token.TokenType.For){
                forCounter++;
                forList.add(forCounter);
                forNodeList.add(currentNode.getLevel());
            }
            currentNode.setForBlocks((ArrayList<Integer>) forList.clone());
            if(currentNode.getTokenType() != null && currentNode.getTokenType() == Token.TokenType.Identifier){
                updateValueInfos(currentNode);
            }
            currentNode = currentNode.nextNode();
        }
    }

    private void updateValueInfos(Node currentNode){
        int currentLoop = -1;
        if(currentNode.getForBlocks().size() != 0) currentLoop = currentNode.getForBlocks().get(currentNode.getForBlocks().size() - 1);
        if(currentNode.getParentNode().getType() != null && currentNode.getParentNode().getType().equals("variable initialization")){
            String type = currentNode.getParentNode().getParentNode().getTokenType().toString().toLowerCase();
            valueInfos.put(currentNode.getValue(), new ValueInfo(type, currentLoop));
        }
        if(currentNode.getParentNode().getType() != null && currentNode.getParentNode().getType().equals("assign") && currentLoop != -1){
            valueInfos.get(currentNode.getValue()).addToList(currentLoop);
        }
    }

    private void lookForExpressions(){
        Node currentNode = syntaxTree.getRoot();
        boolean skip  = false;
        while (currentNode != null){
            if(currentNode.getForBlocks() == null || currentNode.getForBlocks().size() == 0){
                currentNode  = currentNode.nextNode();
                continue;
            }
            if(currentNode.getType() != null && (currentNode.getType().equals("start statement")
                    || currentNode.getType().equals("condition") || currentNode.getType().equals("post statement"))) skip = true;
            if(currentNode.getType() != null && (currentNode.getType().equals("block"))) skip = false;
            if(!skip && currentNode.getType() != null && currentNode.getType().equals("expression")){
                int level = currentNode.getLevel();
                checkExpression(currentNode);
                do currentNode = currentNode.nextNode();
                while(currentNode.getLevel() > level);
            }
            else currentNode  = currentNode.nextNode();
        }
    }

    private void checkExpression(Node currentNode){
        int numberOfCalculations = countLogicOperators(currentNode) + 1;
        ArrayList<Node>[] calculations = new ArrayList[numberOfCalculations];
        for(int i = 0; i < calculations.length; i++) calculations[i] = new ArrayList<>();
        factorize(currentNode, calculations);
        for (ArrayList calculation : calculations) {
            markElements(calculation);
            optimizeElements(calculation, currentNode);
            optimizeFactors(calculation, currentNode);
        }

    }

    private int countLogicOperators(Node currentNode){
        int count = 0;
        for (Node childNode : currentNode.getChildNodes()) {
            if(childNode.getTokenType() != null && isLogicOperator(childNode.getTokenType())) count++;
        }
        return count;
    }

    private void optimizeElements(ArrayList<Node> elements, Node currentNode){
        int counter = 0;
        for (Node element : elements){
            if(!element.getType().equals("op") && element.getValue().equals("movable")) {
                counter++;
                if((element.getChildNodes().get(0).getType() != null && (element.getChildNodes().get(0).getType().equals("expression"))) ||
                        (element.getChildNodes().get(0).getTokenType() != null &&
                                element.getChildNodes().get(0).getTokenType() == Token.TokenType.Negation) ||
                        ((element.getChildNodes().get(0).getTokenType() == Token.TokenType.Plus
                                || element.getChildNodes().get(0).getTokenType() == Token.TokenType.Minus) &&
                                element.getChildNodes().get(0).getChildNodes().size() != 0)) counter++;
            }
        }
        if(counter > 1) {
            optimizeElement(currentNode, elements);
        }
    }

    private void optimizeElement(Node currentNode, ArrayList<Node> elements){
        Node last = null;
        Node exp = new Node(null, "expression");
        int i = 0;
        int id = 0;
        for(;id < currentNode.getChildNodes().size(); id++){
            if(currentNode.getChildNodes().get(id) == elements.get(0).getChildNodes().get(0)) break;
        }
        for (Node element : elements) {
            if(!element.getType().equals("op") && element.getValue().equals("movable")){
                if(i != 0) {
                    exp.addNewChild(elements.get(i-1).getChildNodes().get(0));
                    elements.get(i-1).setValue("moved");
                }
                for (Node childNode : element.getChildNodes()) {
                    exp.addNewChild(childNode);
                    elements.get(i).setValue("moved");
                }
            }
            else if(element.getType().equals("exp")) last  = element;
            i++;
        }
        editExp(id, last, currentNode, exp, 0);
    }

    private void optimizeFactors(ArrayList<Node> elements, Node currentNode){
        for (Node element : elements) {
            if(!element.getType().equals("op") && !element.getValue().equals("moved")){
                int counter= 0;
                for (Node childNode : element.getChildNodes()) {
                    if (childNode.getTokenType() != null) {
                        if (childNode.getTokenType() == Token.TokenType.Identifier && !checkIfGenerated(childNode.getValue()) &&
                                checkIfMovable(childNode, childNode.getForBlocks().get(childNode.getForBlocks().size() - 1)))
                            counter++;
                        else if (childNode.getTokenType() == Token.TokenType.Number || childNode.getTokenType() == Token.TokenType.FiniteNumber
                                || childNode.getTokenType() == Token.TokenType.True || childNode.getTokenType() == Token.TokenType.False)
                            counter++;
                    }
                    if(childNode.getType() != null && childNode.getType().equals("expression") && childNode.getValue().equals("movable"))
                        counter += 2;
                }
                if(counter > 1){
                    optimizeFactor(currentNode, element);
                }
            }
        }
    }

    private void optimizeFactor(Node currentNode, Node element){
        Node last = null;
        Node partExp = new Node(null, "expression");
        int j = 0;
        int id = 0;
        for(;id < currentNode.getChildNodes().size(); id++){
            if(currentNode.getChildNodes().get(id) == element.getChildNodes().get(0)) break;
        }
        for (Node childNode : element.getChildNodes()) {
            if(childNode.getTokenType() != null) {
                if (childNode.getTokenType() == Token.TokenType.Identifier) {
                    if (checkIfMovable(childNode, childNode.getForBlocks().get(childNode.getForBlocks().size() - 1))){
                        if (j != 0) partExp.addNewChild(element.getChildNodes().get(j - 1));
                        partExp.addNewChild(childNode);
                    }
                    else last = childNode;
                }
                if (childNode.getTokenType() == Token.TokenType.Number || childNode.getTokenType() == Token.TokenType.FiniteNumber
                        || childNode.getTokenType() == Token.TokenType.True || childNode.getTokenType() == Token.TokenType.False) {
                    if (j != 0) partExp.addNewChild(element.getChildNodes().get(j - 1));
                    partExp.addNewChild(childNode);
                }
            }
            if(childNode.getType() != null && childNode.getType().equals("expression")){
                if(childNode.getValue().equals("movable")){
                    if(j!=0) partExp.addNewChild(element.getChildNodes().get(j-1));
                    partExp.addNewChild(childNode);
                }
                else last = childNode;
            }
            j++;
        }
        editExp(id, last, currentNode, partExp, 1);
        checkFirstSign(currentNode, 0);
    }

    private void editExp(int id, Node last, Node currentNode, Node exp, int mode){
        String name = generateName();
        Node tmp;
        if(mode ==0) tmp = new Node(null, Token.TokenType.Plus, null);
        else tmp = new Node(null, Token.TokenType.Multiply, null);
        Node tmp2 = new Node(null, Token.TokenType.Identifier, name);
        tmp.setParentNode(currentNode);
        tmp2.setParentNode(currentNode);
        int k = 0;
        if(last != null) for(;k < currentNode.getChildNodes().size(); k++){
            if(currentNode.getChildNodes().get(k) == last) break;
        }
        else k = id-1;
        currentNode.getChildNodes().add(k + 1, tmp);
        currentNode.getChildNodes().add(k + 2, tmp2);
        insert(currentNode.getForBlocks().get(currentNode.getForBlocks().size()-1), exp, name);
    }

    private void fixSigns(){
        Node currentNode = syntaxTree.getRoot();
        while (currentNode != null){
            if(currentNode.getTokenType() != null && (currentNode.getTokenType() == Token.TokenType.Plus
                    || currentNode.getTokenType() == Token.TokenType.Minus) && currentNode.getChildNodes().size() == 0){
                Node tmp = currentNode.nextNode();
                Node parent = tmp.getParentNode();
                int i = 0;
                for (Node childNode : parent.getChildNodes()) {
                    if(childNode == tmp) break;
                    i++;
                }
                checkFirstSign(parent, i);
            }
            currentNode = currentNode.nextNode();
        }
    }

    private void checkFirstSign(Node currentNode, int index){
        if(currentNode.getChildNodes().get(index).getTokenType() != null){
            if(currentNode.getChildNodes().get(index).getTokenType() == Token.TokenType.Multiply){
                currentNode.getChildNodes().remove(index);
            }
            if(currentNode.getChildNodes().get(index).getTokenType() == Token.TokenType.Divide){
                Node tmp = new Node(null, Token.TokenType.Number, "1");
                tmp.setParentNode(currentNode);
                currentNode.getChildNodes().add(index, tmp);
            }
        }
    }

    private void factorize(Node currentNode, ArrayList[] calculations){
        Node tmp = new Node(null, "exp");
        int i = 0;
        calculations[i].add(tmp);
        for (Node childNode : currentNode.getChildNodes()) {
            if(childNode.getTokenType() != null && isLogicOperator(childNode.getTokenType())) {
                i++;
                tmp = new Node(null, "exp");
                calculations[i].add(tmp);
            }
            if(childNode.getTokenType() != null && (childNode.getTokenType() == Token.TokenType.Plus
                    || childNode.getTokenType() == Token.TokenType.Minus) && childNode.getChildNodes().size() == 0){
                Node tmp2 = new Node(null, "op");
                tmp2.addFakeChild(childNode);
                calculations[i].add(tmp2);
                tmp = new Node(null, "exp");
                calculations[i].add(tmp);
            }
            else {
                if(!(childNode.getTokenType() != null && isLogicOperator(childNode.getTokenType()))) tmp.addFakeChild(childNode);
            }
        }
    }

    private boolean isLogicOperator(Token.TokenType tokenType){
        return tokenType == Token.TokenType.Equal || tokenType == Token.TokenType.NotEqual ||
                tokenType == Token.TokenType.Or || tokenType == Token.TokenType.And
                || tokenType == Token.TokenType.Greater || tokenType == Token.TokenType.GreaterOrEqual
                || tokenType == Token.TokenType.Less || tokenType == Token.TokenType.LessOrEqual;
    }

    private void markElements(ArrayList<Node> elements){
        for (Node element : elements) {
            boolean movable = true;
            if(element.getType().equals("op")) continue;
            for (Node childNode : element.getChildNodes()) {
                if(childNode.getTokenType() != null && childNode.getTokenType() == Token.TokenType.Identifier){
                    if(checkIfGenerated(childNode.getValue()) ||
                            !checkIfMovable(childNode, childNode.getForBlocks().get(childNode.getForBlocks().size()-1))) movable = false;
                }
                if(childNode.getType() != null && childNode.getType().equals("expression")) {
                    checkBrace(childNode);
                    if(childNode.getValue().equals("unmovable")) movable = false;
                }
                if(childNode.getTokenType() != null && (childNode.getTokenType() == Token.TokenType.Negation ||
                        ((childNode.getTokenType() == Token.TokenType.Plus || childNode.getTokenType() == Token.TokenType.Minus) &&
                                childNode.getChildNodes().size() != 0)))  {
                    Node tmp = childNode.getChildNodes().get(0);
                    if(tmp.getType() != null && tmp.getType().equals("expression")) {
                        checkBrace(tmp);
                        if(tmp.getValue().equals("unmovable")) movable = false;
                    }
                    else if(tmp.getTokenType() != null && tmp.getTokenType() == Token.TokenType.Identifier){
                        if(checkIfGenerated(childNode.getValue()) ||
                                !checkIfMovable(tmp, tmp.getForBlocks().get(childNode.getForBlocks().size()-1))) movable = false;
                    }
                }
            }
            if(movable) element.setValue("movable");
            else element.setValue("unmovable");
        }
    }

    private void checkBrace(Node expression){
        for (Node childNode : expression.getChildNodes()) {
            if(childNode.getType() != null && childNode.getType().equals("expression")) checkBrace(childNode);
        }
        boolean movable = true;
        for (Node childNode : expression.getChildNodes()) {
            if((childNode.getType() != null && childNode.getType().equals("expression") && !childNode.getValue().equals("movable"))
                    || (childNode.getTokenType() != null &&( isLogicOperator(childNode.getTokenType())
                    || childNode.getTokenType() == Token.TokenType.Identifier && (checkIfGenerated(childNode.getValue())
                    || !checkIfMovable(childNode, childNode.getForBlocks().get(childNode.getForBlocks().size()-1)))))) movable = false;
        }
        if(movable) expression.setValue("movable");
        else {
            expression.setValue("unmovable");
            checkExpression(expression);
        }
    }

    private void insert(int loop, Node expression, String name){
        checkFirstSign(expression, 0);
        loop = determineLoop(loop, expression);
        Node currentNode = syntaxTree.getRoot();
        while (currentNode != null){
            if(currentNode.getTokenType() != null && currentNode.getTokenType() == Token.TokenType.For
                    && currentNode.getForBlocks().get(currentNode.getForBlocks().size()-1) == loop){
                Node tmp = currentNode.getParentNode();
                int i = 0;
                for(; i < tmp.getChildNodes().size(); i++){
                    if(tmp.getChildNodes().get(i) == currentNode) break;
                }
                tmp.getChildNodes().add(i, createInitialization(tmp, expression, name));
                syntaxTree.getRoot().calculateLevel(0);
                break;
            }
            currentNode = currentNode.nextNode();
        }
    }

    private int determineLoop(int loop, Node expression){
        Node currentNode = syntaxTree.getRoot();
        while (currentNode != null) {
            if (currentNode.getTokenType() != null && currentNode.getTokenType() == Token.TokenType.For
                    && currentNode.getForBlocks().get(currentNode.getForBlocks().size() - 1) == loop) break;
            currentNode = currentNode.nextNode();
        }
        assert currentNode != null;
        for(int i = currentNode.getForBlocks().size() - 2; i >= 0; i--){
            int tmp = currentNode.getForBlocks().get(i);
            Node currentNode2 = expression;
            while (currentNode2 != null) {
                if (currentNode2.getTokenType() != null && currentNode2.getTokenType() == Token.TokenType.Identifier){
                    if(!checkIfMovable(currentNode2, tmp)) return loop;
                }
                currentNode2 = currentNode2.nextNode();
            }
            loop = tmp;
        }
        return loop;
    }

    private Node createInitialization(Node parent, Node expression, String name){
        Node result = new Node(null, determineType(expression), null);
        result.setParentNode(parent);
        Node tmp = new Node(result, "variable initialization");
        new Node(tmp, Token.TokenType.Identifier, name);
        new Node(tmp, Token.TokenType.Assign, null);
        tmp.addNewChild(expression);
        return result;
    }

    private Token.TokenType determineType(Node exp){
        Token.TokenType result = Token.TokenType.Bool;
        while(exp != null){
            if(exp.getTokenType() != null){
                switch (exp.getTokenType()){
                    case Identifier:
                        switch (valueInfos.get(exp.getValue()).getType()){
                            case "double":
                                return Token.TokenType.Double;
                            case "int":
                                result = Token.TokenType.Int;
                                break;
                            case "float":
                                result = Token.TokenType.Float;
                                break;
                            case "short":
                                result = Token.TokenType.Short;
                                break;
                        }
                        break;
                    case Number:
                        result = Token.TokenType.Int;
                        break;
                    case FiniteNumber:
                        return Token.TokenType.Double;
                }
            }
            exp = exp.nextNode();
        }
        return  result;
    }
    private boolean checkIfMovable(Node node, int loop){
        ArrayList<Integer> tmp = valueInfos.get(node.getValue()).getAssignList();
        for (Integer integer : tmp) {
            if(integer == loop) return false;
        }
        if(node.getChildNodes().size() != 0){
            int numberOfCalculations = countLogicOperators(node) + 1;
            ArrayList<Node>[] calculations = new ArrayList[numberOfCalculations];
            for(int i = 0; i < calculations.length; i++) calculations[i] = new ArrayList<>();
            factorize(node.getChildNodes().get(0), calculations);
            boolean movable = true;
            for (ArrayList<Node> calculation : calculations) {
                markElements(calculation);
                for (Node element : calculation) {
                    if (element.getType().equals("exp") && !element.getValue().equals("movable")) movable = false;
                }
            }
            if(movable) return true;
            else{
                checkExpression(node.getChildNodes().get(0));
                return false;
            }
        }
        return true;
    }

    private String generateName(){
        String name = "gen" + generated;
        generated++;
        while(!checkIfNameFree(name)){
            name = "gen" + generated;
            generated++;
        }
        generatedNames.add(name);
        return name;
    }
    private boolean checkIfNameFree(String name){
        return valueInfos.get(name) == null;
    }

    private boolean checkIfGenerated(String name){
        for (String generatedName : generatedNames) {
            if(generatedName.equals(name)) return true;
        }
        return false;
    }
}
