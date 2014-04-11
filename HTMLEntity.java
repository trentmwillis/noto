public enum HTMLEntity {
    AMPERSAND ("&", "&amp;"),
    LT ("<", "&lt;"),
    GT (">", "&gt;");

    /* Data Members */

    private String symbol, entity;

    /* Constructors */

    HTMLEntity(String symbol, String entity) {
        this.symbol = symbol;
        this.entity = entity;
    }

    /* Accessor Methods */

    public String getSymbol() {
        return symbol;
    }

    public String getEntity() {
        return entity;
    }
}
