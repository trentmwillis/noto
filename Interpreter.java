import java.io.*;
import java.util.Stack;
import java.util.Scanner;


public class Interpreter {
    private static Interpreter instance;

    private static Stack<String> tags = new Stack<String>();
    private static StringBuilder output;
    private static Scanner scanner;
    private static boolean lastLineEmpty = false;
    private static HTMLElement currentLineType;
    private static HTMLElement lastLineType;

    protected Interpreter() { /* Nothing */ }

    /* Public Methods */

    public static Interpreter getInstance() {
        if (instance == null) {
            instance = new Interpreter();
        }

        return instance;
    }

    public static void reset() {
        output = new StringBuilder();
        tags.removeAllElements();
    }

    public static String getOutput() {
        // Closes any open tags
        while (!tags.empty()) {
            closeTag(tags.pop());
        }

        // Return output as String
        return output.toString();
    }

    public static void interpret(String line) {
        // Check beginning of each word for tags
        scanner = new Scanner(line);

        // Check beginning of line for block style
        if (scanner.hasNext()) {
            String start = scanner.next();
            lastLineType = currentLineType;
            currentLineType = getBlockType(start);

            if (currentLineType != lastLineType) {
                closeLastTag();
                openTag(currentLineType.getTag());
            } else {
                output.append("<br>");
            }

            if (currentLineType == HTMLElement.P) {
                output.append(start + " ");
            }

            // Loop through each token
            while (scanner.hasNext()) {
                String token = scanner.next();

                HTMLElement inlineType = getInlineOpenType(token);
                if (inlineType != null) {
                    openTag(inlineType.getTag());
                }

                output.append(token + " ");

                inlineType = getInlineCloseType(token);
                if (inlineType != null) {
                    closeLastTag();
                }
            }
        } else {
            closeLastTag();
            lastLineEmpty = true;
        }
    }

    /* Private Methods */

    private static HTMLElement getBlockType(String token) {
        for  (HTMLElement element : Html.BLOCK_ELEMENTS) {
            if (token.equals(element.getSymbol())) {
                return element;
            }
        }

        return HTMLElement.P;
    }

    private static HTMLElement getInlineOpenType(String token) {
        for (HTMLElement element : Html.INLINE_ELEMENTS) {
            if (token.startsWith(element.getSymbol())) {
                return element;
            }
        }
        return null;
    }
    private static HTMLElement getInlineCloseType(String token) {
        for (HTMLElement element : Html.INLINE_ELEMENTS) {
            if (token.endsWith(element.getSymbol())) {
                return element;
            }
        }
        return null;
    }

    private static void openTag(String tag) {
        tags.push(tag);
        output.append("<" + tag + ">");
    }

    private static void closeTag(String tag) {
        output.append("</" + tag + ">");
    }

    private static void closeLastTag() {
        if (!tags.empty()) {
            closeTag(tags.pop());
        }
    }
}