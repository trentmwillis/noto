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

        if (!scanner.hasNext()) {
            if (!tags.empty()) {
                closeTag(tags.pop());
            }
            lastLineEmpty = true;
            return;
        }

        // Check beginning of line for block style
        if (scanner.hasNext()) {
            String start = scanner.next();
            lastLineType = currentLineType;
            currentLineType = getBlockType(start);

            if (currentLineType != lastLineType) {
                openTag(currentLineType.getTag());
            }

            // Loop through each token
            while (scanner.hasNext()) {
                output.append(scanner.next());
            }
        }
    }

    /* Private Methods */

    private static HTMLElement getBlockType(String token) {
        for  (HTMLElement element : HTMLElement.values()) {
            if (token.equals(element.getSymbol())) {
                return element;
            }
        }

        return HTMLElement.P;
    }

    private static void openTag(String tag) {
        tags.push(tag);
        output.append("<" + tag + ">");
    }

    private static void closeTag(String tag) {
        output.append("</" + tag + ">");
    }
}