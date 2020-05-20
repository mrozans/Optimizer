package SemanticAnalizer;

class VariableInfo {
    private String variable;
    private boolean initialized;
    private boolean array;

    VariableInfo(String variable, boolean array) {
        this.variable = variable;
        initialized = false;
        this.array = array;
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

    boolean isArray() {
        return array;
    }
}
