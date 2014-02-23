import java.io.*;
import java.util.Stack;
import java.util.Scanner;


public class Interpreter {
    private static Interpreter instance;

    private static Stack<HTMLElement> tags = new Stack<HTMLElement>();
    private static StringBuilder output = new StringBuilder();
    private static Scanner scanner;
    private static boolean lastLineEmpty = false;
    private static boolean inTable = false;
    private static boolean inOrderedList = false;

    private static HTMLElement currentLineType;
    private static HTMLElement lastLineType;

    private static boolean buildingDiagram = false;

    protected Interpreter() { /* Nothing */ }

    /* Public Methods */

    public static Interpreter getInstance() {
        if (instance == null) {
            instance = new Interpreter();
        }

        return instance;
    }

    public static void reset() {
        output.setLength(0);
        tags.removeAllElements();
        lastLineEmpty = false;
        inTable = false;
        inOrderedList = false;
        buildingDiagram = false;
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

        // Check if line is empty
        if (scanner.hasNext()) {



            // Set the "start" to the first token in the line
            String start = scanner.next();

            // Check if the line starts a diagram
            DiagramType diaType;
            if ((diaType = isDiagramDeclaration(start)) != null || buildingDiagram) {

                // If the parser isn't currently building, start a new diagram
                if (!Parser.getInstance().isBuilding()) {
                    buildingDiagram = true;
                    Parser.getInstance().reset();
                    Parser.getInstance().newDiagram(diaType);
                }

                // Otherwise...
                else {
                    // Add data to the diagram being built
                    Parser.getInstance().addData(line);

                    // See if the data just passed was the final line
                    if (Parser.getInstance().complete()) {
                        output.append("<img src='" + Parser.getImage() + "'>");
                        buildingDiagram = false;
                    }
                }

                return;
            }

            // Update the last line type
            lastLineType = currentLineType;

            // Get the current line's type by passing the start token
            currentLineType = getBlockType(start);

            // Make sure lastLineEmpty is set to false if there was a last line
            if (lastLineEmpty) {
                lastLineEmpty = false;
            }

            // If the LineType changed, then we should close the last block
            if (currentLineType != lastLineType && !inOrderedList) {
                closeLastTag();

                // Also, if the last line was a list close the item
                if (isListLine(lastLineType)) {
                    closeLastTag();
                }


                if (currentLineType == HTMLElement.TABLE) {
                    inTable = (inTable) ? false : true;
                    if (!inTable) {
                        closeLastTag();
                    }
                }

                // Finally, open the new element tag
                openTag(currentLineType);
            } else if (inOrderedList) {
                currentLineType = HTMLElement.OL;
            } else if (isListLine(lastLineType)) {
                closeLastTag();
            } else {
                output.append("<br>");
            }

            if (currentLineType == HTMLElement.OL || currentLineType == HTMLElement.OL2 || currentLineType == HTMLElement.OL3) {
                inOrderedList = true;
            }

            // If this is a list line, open up a new item
            if (isListLine(currentLineType)) {
                openTag(HTMLElement.LI);
            }

            // If the line is a PRE line, we just want to output the entire line
            if (currentLineType == HTMLElement.PRE) {
                scanner.useDelimiter("\n");
                if (scanner.hasNext()) {
                    output.append(scanner.next());
                }
                scanner.reset();
            }

            // Else, just parse the line normally
            else {
                boolean firstOfPara = (currentLineType == HTMLElement.P);
                String token;

                // Loop through each token
                while (scanner.hasNext() || firstOfPara) {

                    // If we are on the first token of a paragraph line, we need to output that
                    if (firstOfPara) {
                        token = start;
                        firstOfPara = false;
                    }

                    // Otherwise, the token is just set to the next value from the scanner
                    else {
                        token = scanner.next();
                    }

                    // Check if this token has any opening inline style
                    HTMLElement inlineType = getInlineOpenType(token);
                    if (inlineType != null) {
                        // Open the inline style
                        openTag(inlineType);

                        //Remove the symbol from the front of the token
                        token = token.substring(inlineType.getSymbol().length());
                    }

                    // Check if this token has any closing inline styles
                    inlineType = getInlineCloseType(token);
                    if (inlineType != null) {
                        int offset = token.length() - inlineType.getEndSymbol().length();
                        // Check if the token ends with a period
                        // and chop of the symbol as appropriate
                        if (token.endsWith(".")) {
                            token = token.substring(0, offset-1) + ".";
                        } else {
                            token = token.substring(0, offset);
                        }

                        // Output the token
                        output.append(token);

                        // Close the inline style
                        closeLastTag();

                        // Add the space back
                        output.append(" ");
                    } else {
                        // Output the token and add the space back
                        output.append(token + " ");
                    }
                }
            }
        } 

        // An empty line, so close the last block if the last line was part of a block
        else {
            if (!lastLineEmpty) {
                closeLastTag();
            }

            if (inOrderedList) {
                inOrderedList = false;
            }

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
               line == HTMLElement.OL ||
               line == HTMLElement.OL2 ||
               line == HTMLElement.OL3;
    }

    private static DiagramType isDiagramDeclaration(String token) {
        for (DiagramType type : Diagram.TYPES) {
            for (int i=0; i<type.getDefinitions().length; i++) {
                if (token.equals("_" + type.getDefinitions()[i])) {
                    System.out.println(type.getDefinitions()[i]);
                    return type;
                }
            }
        }
        return null;
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
