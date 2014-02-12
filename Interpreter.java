import java.io.*;
import java.util.Stack;
import java.util.Scanner;


public class Interpreter {
    private static Interpreter instance;

    private static Stack<HTMLElement> tags = new Stack<HTMLElement>();
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

            // If the LineType changed, then we know the last block closed
            // Otherwise, it was simply a line-break
            if (currentLineType != lastLineType) {
                closeLastTag();

                if (isListLine(lastLineType)) {
                    closeLastTag();
                }

                openTag(currentLineType);
            } else if (isListLine(lastLineType)) {
                closeLastTag();
            } else {
                output.append("<br>");
            }

            if (isListLine(currentLineType)) {
                openTag(HTMLElement.LI);
            }

            boolean firstOfPara = (currentLineType == HTMLElement.P);
            String token;

            // If the line is a PRE line, we just want to output the entire line
            if (currentLineType == HTMLElement.PRE) {
                scanner.useDelimiter("\n");
                if (scanner.hasNext()) {
                    output.append(scanner.next());
                }
                scanner.reset();
            } else {
                // Loop through each token
                while (scanner.hasNext() || firstOfPara) {
                    if (firstOfPara) {
                        token = start;
                        firstOfPara = false;
                    } else {
                        token = scanner.next();
                    }

                    HTMLElement inlineType = getInlineOpenType(token);
                    if (inlineType != null) {
                        openTag(inlineType);

                        token = token.substring(inlineType.getSymbol().length());
                    }

                    inlineType = getInlineCloseType(token);
                    if (inlineType != null) {
                        int offset = token.length() - inlineType.getEndSymbol().length();
                        if (token.endsWith(".")) {
                            token = token.substring(0, offset-1) + ".";
                        } else {
                            token = token.substring(0, offset);
                        }
                        output.append(token);
                        closeLastTag();
                        output.append(" ");
                    } else {
                        output.append(token + " ");
                    }
                }
            }
        } else {
            closeLastTag();
            lastLineType = currentLineType;
            currentLineType = null;
            lastLineEmpty = true;
        }
    }

    /* Private Methods */

    private static boolean isListLine(HTMLElement line) {
        return line == HTMLElement.UL ||
               line == HTMLElement.UL2 ||
               line == HTMLElement.UL3 ||
               line == HTMLElement.UL4 ||
               line == HTMLElement.OL;
    }

    private static boolean checkForDiagram(String token) {
        return false;
    }

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
            if (token.endsWith(element.getEndSymbol()) || token.endsWith(element.getEndSymbol() + ".")) {
                return element;
            }
        }
        return null;
    }

    private static void openTag(HTMLElement tag) {
        tags.push(tag);
        output.append(tag.getTag());
    }

    private static void closeTag(HTMLElement tag) {
        output.append(tag.getCloseTag());
    }

    private static void closeLastTag() {
        if (!tags.empty()) {
            closeTag(tags.pop());
        }
    }
}
