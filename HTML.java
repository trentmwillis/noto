public class Html {
    public static final HTMLElement[] BLOCK_ELEMENTS = {
        HTMLElement.P,
        HTMLElement.H1,
        HTMLElement.H2,
        HTMLElement.H3,
        HTMLElement.H4,
        HTMLElement.H5,
        HTMLElement.H6,
        HTMLElement.PRE,
        HTMLElement.BLOCKQUOTE,
        HTMLElement.HR,
        HTMLElement.HRD,
        HTMLElement.UL,
        HTMLElement.UL2,
        HTMLElement.UL3,
        HTMLElement.UL4,
        HTMLElement.OL,
        HTMLElement.OL2,
        HTMLElement.OL3,
        HTMLElement.TABLE,
        HTMLElement.TRH,
        HTMLElement.TR
    };

    public static final HTMLElement[] INLINE_ELEMENTS = {
        HTMLElement.STRONG,
        HTMLElement.EM,
        HTMLElement.SUB,
        HTMLElement.SUP,
        HTMLElement.SMALL,
        HTMLElement.MARK,
        HTMLElement.CODE,
        HTMLElement.UNDERLINE,
        HTMLElement.BI,
        HTMLElement.IB,
        HTMLElement.INLINEQUOTE,
        HTMLElement.TH,
        HTMLElement.TD
    };

    public static String head(String pageTitle) {
        return "<html><head><meta charset='utf-8'><title>" + pageTitle + "</title><link rel='stylesheet' href='css/styles.css'><link rel='stylesheet' href='http://yandex.st/highlightjs/8.0/styles/default.min.css'></head><body><div id='content'>";
    }

    public static final String HEAD = "<html><head><meta charset='utf-8'><title>Noto Note</title><link rel='stylesheet' href='styles.css'><link rel='stylesheet' href='http://yandex.st/highlightjs/8.0/styles/default.min.css'></head><body><div id='content'>";
    public static final String END = "</div><script src='http://yandex.st/highlightjs/8.0/highlight.min.js'></script><script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js'></script><script>$(document).ready(function() {$('pre').each(function(i, e) {hljs.highlightBlock(e)});});</script></body>";

    public static final String CSS = "body{font:100%/1.5 'Helvetica','Arial',sans-serif;color:#444}blockquote{margin:0 0 1em;padding:.5em 1.5em;border-left:.25em solid #DDD;color:#777;background:#efefef;font-style:italic;position:relative}blockquote:before{content:'\201C';font-size:2em;line-height:46px;display:block;position:absolute;height:32px;width:32px;background:#DDD;border-radius:99px;text-indent:6px;left:-0.5em}mark{background:#e0f268;padding:.1em .25em;border-radius:.25em}pre{border-left:.25rem solid #DDD}#content{max-width:960px;margin:2em auto}ul.circle{list-style:circle}ul.square{list-style:square}ol.lower-alpha{list-style:lower-alpha}ol.upper-alpha{list-style:upper-alpha}hr{border:0;border-top:1px solid #CCC;margin:2em 0}hr.double{height:2px;border-bottom:1px dotted #CCC;border-top-color:#AAA}u{text-decoration:none;border-bottom:1px dotted #555}q:before{content:'\201C'}q:after{content:'\201D'}";

    public static final int PAGE_WIDTH = 960;
}
