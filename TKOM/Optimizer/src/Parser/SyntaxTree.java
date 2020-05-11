package Parser;

public class SyntaxTree {
    private Node root;
    private boolean valid;

    SyntaxTree() {
        root = new Node();
        valid = true;
    }

    public void printTree(){
        root.printNode();
    }

    Node getRoot() {
        return root;
    }

    public boolean isValid() {
        return valid;
    }

    void setValid(boolean valid) {
        this.valid = valid;
    }
}
