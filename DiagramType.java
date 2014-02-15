public enum DiagramType {
    TREE ("tree"),
    FLOWCHART ("flowchart", "fc"),
    NETWORK ("network", "nw");

    private String[] definitions;

    DiagramType(String... definitions) {
        this.definitions = definitions;
    }

    public String[] getDefinitions() { return definitions; }

    public boolean isDiagramDeclaration(String token) {
        if (token.charAt(0) != '_') { return false; }
        for (DiagramType type : DiagramType.values()) {
            for (int i=0; i<type.definitions.length; i++) {
                if (token.equals("_" + type.definitions[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}