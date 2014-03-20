import java.io.File;

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
        return "<html><head><meta charset='utf-8'><title>" + pageTitle + "</title><link rel='stylesheet' href='css/styles.min.css'><link rel='stylesheet' href='css/highlight.min.css'></head><body><div id='content'>";
    }

    public static String navigation(String directory, String currentFile) {
        File folder = new File(directory);
        File[] files = folder.listFiles();
        StringBuilder navigation = new StringBuilder("<ul id='nav'>");

        // Loop through and add each .txt file to the navigation
        for (int i=0; i<files.length; i++) {
            int index = files[i].getName().lastIndexOf('.');
            // Check to make sure it is a .txt file
            if (files[i].isFile() && files[i].getName().substring(index+1).equals("txt")) {
                String name = files[i].getName().substring(0, index);
                if (name.equals(currentFile)) {
                    navigation.append("<li class='active'><a href='" + name + ".html'>" + name + "</a></li>");
                } else {
                    navigation.append("<li><a href='" + name + ".html'>" + name + "</a></li>");
                }   
            }
        }

        navigation.append("</ul>");

        return navigation.toString();
    }

    public static final String END = "</div><script src='scripts/highlight.min.js'></script><script src='scripts/jquery.min.js'></script><script>$(document).ready(function() {$('pre').each(function(i, e) {hljs.highlightBlock(e)});});</script></body>";
    public static final int PAGE_WIDTH = 960;
}
