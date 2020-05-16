package SemanticAnalizer;

class Pair {
    private String variable;
    private boolean initialized;

    Pair(String variable) {
        this.variable = variable;
        initialized = false;
    }

    String getVariable() {
        return variable;
    }

    boolean isInitialized() {
        return initialized;
    }

    void setInitialized() {
        this.initialized = true;
    }
}
