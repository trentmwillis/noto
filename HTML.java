public class Html {
    public static final HTMLElement[] BLOCK_ELEMENTS = {
        HTMLElement.H1,
        HTMLElement.H2,
        HTMLElement.H3,
        HTMLElement.H4,
        HTMLElement.H5,
        HTMLElement.H6,
        HTMLElement.P,
        HTMLElement.PRE,
        HTMLElement.BLOCKQUOTE,
        HTMLElement.HR
    };

    public static final HTMLElement[] INLINE_ELEMENTS = {
        HTMLElement.STRONG,
        HTMLElement.EM,
        HTMLElement.SUB,
        HTMLElement.SUP,
        HTMLElement.SMALL,
        HTMLElement.MARK,
        HTMLElement.CODE,
        HTMLElement.UNDERLINE
        //HTMLElement.BI,
        //HTMLElement.IB,
    };

    public static final String HEAD = "<html><head><meta charset='utf-8'><title>Noto Note</title><link rel='stylesheet' href='styles.css'><link rel='stylesheet' href='http://yandex.st/highlightjs/8.0/styles/default.min.css'></head><body><div id='content'>";
    public static final String END = "</div><script src='http://yandex.st/highlightjs/8.0/highlight.min.js'></script><script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js'></script><script>$(document).ready(function() {$('pre').each(function(i, e) {hljs.highlightBlock(e)});});</script></body>";

    public static final String CSS = "String";
}
