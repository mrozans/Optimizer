package SemanticAnalizer;

import Parser.Node;

class NodeIterator {
    private Node currentNode;
    private int lastCheckedChild;

    NodeIterator(Node currentNode) {
        this.currentNode = currentNode;
        lastCheckedChild = -1;
    }

    void step(){
        if(lastCheckedChild != currentNode.getChildNodes().size()-1){
            currentNode = currentNode.getChildNodes().get(lastCheckedChild + 1);
            lastCheckedChild = -1;
        }
        else {
            Node tmp = currentNode.getParentNode();
            if(tmp == null) {
                currentNode = null;
                return;
            }
            for(int i = 0; ; i++){
                if(tmp.getChildNodes().get(i) == currentNode){
                    lastCheckedChild = i;
                    currentNode = tmp;
                    return;
                }
            }
        }
    }

    Node getCurrentNode() {
        return currentNode;
    }

    boolean notVisited(){
        return lastCheckedChild == -1;
    }

    boolean finished(){
        return lastCheckedChild == currentNode.getChildNodes().size() - 1;
    }
}
