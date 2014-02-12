public enum HTMLElement {
    H1 ("<h1>", "</h1>", "#"),
    H2 ("<h2>", "</h2>", "##"),
    H3 ("<h3>", "</h3>", "###"),
    H4 ("<h4>", "</h4>", "####"),
    H5 ("<h5>", "</h5>", "#####"),
    H6 ("<h6>", "</h6>", "######"),
    P ("<p>", "</p>", ""),
    PRE ("<pre>", "</pre>", "<"),
    BLOCKQUOTE ("<blockquote>", "</blockquote>", ">"),
    HR ("<hr>", "", "---"),

    STRONG ("<strong>", "</strong>", "!!"),
    EM ("<em>", "</em>", "**"),
    SUB ("<sub>", "</sub>", "vv"),
    SUP ("<sup>", "</sup>", "^^"),
    SMALL ("<small>", "</small>", "~~"),
    MARK ("<mark>", "</mark>", "[[", "]]"),
    CODE ("<code>", "</code>", "<<", ">>"),
    UNDERLINE ("<u>", "</u>", "__"),
    BI ("<strong><em>", "</em></strong>", "!*", "*!"),
    IB ("<em><strong>", "</strong></em>", "*!", "!*"),

    UL ("<ul>", "</ul>", "*"),
    UL2 ("<ul class='square'>", "</ul>", "-"),
    UL3 ("<ul class='square'>", "</ul>", "+"),
    UL4 ("<ul class='circle'>", "</ul>", "o"),
    LI ("<li>", "</li>", "*"),

    INLINEQUOTE ("<q>", "</q>", "\"");

    private String tag, closeTag;
    private String symbol, endSymbol;

    HTMLElement(String tag, String symbol) {
        this.tag = tag;
        this.symbol = symbol;
    }

    HTMLElement(String tag, String closeTag, String symbol) {
        this.tag = tag;
        this.closeTag = closeTag;
        this.symbol = symbol;
    }

    HTMLElement(String tag, String closeTag, String symbol, String endSymbol) {
        this.tag = tag;
        this.closeTag = closeTag;
        this.symbol = symbol;
        this.endSymbol = endSymbol;
    }

    public String getTag() { return tag; }
    public String getCloseTag() { return closeTag; }

    public String getSymbol() { return symbol; }
    public String getEndSymbol() {
        return (endSymbol != null) ? endSymbol : getSymbol();
    }
}
