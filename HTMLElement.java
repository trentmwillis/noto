public enum HTMLElement {
    P ("<p>", "</p>", ""),
    H1 ("<h1>", "</h1>", "#"),
    H2 ("<h2>", "</h2>", "##"),
    H3 ("<h3>", "</h3>", "###"),
    H4 ("<h4>", "</h4>", "####"),
    H5 ("<h5>", "</h5>", "#####"),
    H6 ("<h6>", "</h6>", "######"),
    PRE ("<pre>", "</pre>", "<"),
    BLOCKQUOTE ("<blockquote>", "</blockquote>", ">"),
    HR ("<hr>", "", "---"),
    HRD ("<hr class='double'>", "", "==="),

    INLINEQUOTE ("<q>", "</q>", "\""),
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
    OL ("<ol>", "</ol>", "1."),
    OL2 ("<ol class='upper-alpha'>", "</ol>", "A."),
    OL3 ("<ol class='lower-alpha'>", "</ol>", "a."),
    LI ("<li>", "</li>", ""),

    TABLE ("<table>", "</table>", "TT"),
    TRH ("<tr>", "</tr>", "||"),
    TR ("<tr>", "</tr>", "|"),
    TH ("<th>", "</th>", "||"),
    TD ("<td>", "</td>", "|"),

    DIAGRAM;

    /* Data Members */

    private String tag, closeTag;
    private String symbol, endSymbol;

    /* Constructors */
    HTMLElement() { }

    HTMLElement(String tag, String symbol) {
        this.tag = tag;
        this.symbol = symbol;
    }

    HTMLElement(String tag, String closeTag, String symbol) {
        this(tag, symbol);
        this.closeTag = closeTag;
    }

    HTMLElement(String tag, String closeTag, String symbol, String endSymbol) {
        this(tag, closeTag, symbol);
        this.endSymbol = endSymbol;
    }

    /* Accessor Methods */

    public String getTag() {
        return tag;
    }

    public String getCloseTag() {
        return (closeTag != null) ? closeTag : "";
    }

    public String getSymbol() {
        return symbol;
    }

    public String getEndSymbol() {
        return (endSymbol != null) ? endSymbol : getSymbol();
    }
}
