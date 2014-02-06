public enum HTMLElement {
    H1 ("h1", "#"),
    H2 ("h2", "##"),
    H3 ("h3", "###"),
    H4 ("h4", "####"),
    H5 ("h5", "#####"),
    H6 ("h6", "######"),
    P ("p", ""),
    PRE ("pre", "<"),
    BLOCKQUOTE ("blockquote", ">"),
    HR ("hr", "---"),

    STRONG ("strong", "!!"),
    EM ("em", "**"),
    SUB ("sub", "vv"),
    SUP ("sup", "^^"),
    SMALL ("small", "~~"),
    MARK ("mark", "[[", "]]"),
    CODE ("code", "<<", ">>"),
    UNDERLINE ("u", "__");

    private String tag;
    private String symbol, endSymbol;

    HTMLElement(String tag, String symbol) {
        this.tag = tag;
        this.symbol = symbol;
    }

    HTMLElement(String tag, String symbol, String endSymbol) {
        this.tag = tag;
        this.symbol = symbol;
        this.endSymbol = endSymbol;
    }

    public String getTag() { return tag; }
    public String getSymbol() { return symbol; }
    public String getEndSymbol() {
        return (endSymbol != null) ? endSymbol : getSymbol();
    }
}
