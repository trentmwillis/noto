import java.util.Stack;
import java.util.Scanner;

/*
The interpreter class handles the interpretation of each line during
the building process. It will either interpret HTML as needed, or
pass the values to the Parser which constructs diagrams.
*/

public class Interpreter {
    public static final String ESCAPE_CHAR = "\\";

    private static Interpreter instance;

    private boolean buildingDiagram = false;
    private HTMLElement currentLineType;
    private HTMLElement lastLineType;
    private Scanner scanner;
    private Stack<HTMLElement> tags = new Stack<HTMLElement>();
    private StringBuilder output = new StringBuilder();

    protected Interpreter() { }

    public static Interpreter getInstance() {
        if (instance == null) {
            instance = new Interpreter();
        }

        return instance;
    }

    public void reset() {
        output.setLength(0);
        tags.removeAllElements();
        buildingDiagram = false;
        Parser.getInstance().resetHard();
    }

    // Returns the output from the current interpretation
    public String getOutput() {
        // Closes any open tags
        while (!tags.empty()) {
            closeTag(tags.pop());
        }

        // Return output as String
        return output.toString();
    }

    public void interpret(String line) {
        // Check beginning of each word for tags
        scanner = new Scanner(line);

        // Check if line is empty
        if (scanner.hasNext()) {

            // Set the "start" to the first token in the line
            String start = scanner.next();

            // Update the last line type
            lastLineType = currentLineType;

            // Check if the line starts a Diagram or if we are currently constructing a Diagram
            DiagramType diaType = isDiagramDeclaration(start);
            if (diaType != null || buildingDiagram) {
                currentLineType = HTMLElement.DIAGRAM;

                if (lastLineType != HTMLElement.DIAGRAM) {
                    closeLastTag();
                }

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

            // Get the current line's type by passing the start token
            currentLineType = getBlockType(start);

            // If the LineType changed, and we aren't in something like a list 
            // or table then we should close the last block
            if (currentLineType != lastLineType) {
                if (isNonClosingBlock(lastLineType)) {
                    closeLastTag();
                }

                if (isNonClosingBlock(currentLineType)) {
                    openTag(currentLineType);

                    if (isListLine(currentLineType)) {
                        openSelfClosingTag(HTMLElement.LI);
                    } else if (isTableElement(currentLineType)) {
                        openSelfClosingTag(HTMLElement.TR);
                        openSelfClosingTag(HTMLElement.TD);
                    }
                } else {
                    openSelfClosingTag(currentLineType);
                }
            } else if (isListLine(currentLineType)) {
                openSelfClosingTag(HTMLElement.LI);
            } else if (isTableElement(currentLineType)) {
                openSelfClosingTag(HTMLElement.TR);
                openSelfClosingTag(HTMLElement.TD);
            } else {
                output.append("<br>");
            }



            // If the line is a PRE line, we just want to output the entire line
            if (currentLineType == HTMLElement.PRE) {
                scanner.useDelimiter("\n");
                if (scanner.hasNext()) {
                    output.append(scanner.next());
                }
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
                        if (isTableElement(inlineType)) {
                            openSelfClosingTag(inlineType);
                        } else {
                            openTag(inlineType);
                        }

                        //Remove the symbol from the front of the token
                        token = token.substring(inlineType.getSymbol().length());
                    } else if (token.startsWith(Interpreter.ESCAPE_CHAR)) {
                        token = token.substring(1);
                    }

                    // Check for HTML entities
                    token = replaceHTMLEntities(token);

                    // Check if this token has any closing inline styles
                    inlineType = getInlineCloseType(token);
                    int offset;
                    if (inlineType != null && (offset = token.indexOf(Interpreter.ESCAPE_CHAR)) > -1) {
                        output.append(token.substring(0,offset) + token.substring(offset+1) + " ");
                    } else if (inlineType != null) {
                        boolean punctuation = false;
                        offset = token.length() - inlineType.getEndSymbol().length();
                        // Check if the token ends with a period
                        // and chop of the symbol as appropriate
                        if (token.endsWith(".") || token.endsWith(",") || token.endsWith(";")) {
                            offset--;
                            punctuation = true;
                        }

                        output.append(token.substring(0, offset));
                        closeLastTag();

                        if (punctuation) {
                            output.append(token.substring(token.length()-1));
                        }

                        // Add the space back
                        output.append(" ");
                    } else {
                        // Output the token and add the space back
                        output.append(token + " ");
                    }
                }
            }
        } 

        // An empty line, so close the last block if the last line was
        // part of a block that doesn't self-close
        else {
            lastLineType = currentLineType;
            currentLineType = null;

            if (isNonClosingBlock(lastLineType)) {
                closeLastTag();
            }
        }
    }

    /* Private Methods */
    private String replaceHTMLEntities(String token) {
        for (HTMLEntity entity : HTMLEntity.values()) {
            int i = -1;
            int lasti = -1;
            while ((i = token.indexOf(entity.getSymbol(), lasti)) > -1) {
                if (i > 0 && i > lasti) {
                    token = token.substring(0,i) + entity.getEntity() + token.substring(i+1);
                } else if (i == 0 && lasti < 0) {
                    token = entity.getEntity() + token.substring(1);
                }
                lasti = i + 1;
            }
        }

        return token;
    }

    private boolean isNonClosingBlock(HTMLElement line) {
        return line == HTMLElement.PRE ||
               line == HTMLElement.BLOCKQUOTE ||
               line == HTMLElement.H1 ||
               line == HTMLElement.H2 ||
               line == HTMLElement.H3 ||
               line == HTMLElement.H4 ||
               line == HTMLElement.H5 ||
               line == HTMLElement.H6 ||
               line == HTMLElement.UL ||
               line == HTMLElement.UL2 ||
               line == HTMLElement.UL3 ||
               line == HTMLElement.UL4 ||
               line == HTMLElement.OL ||
               line == HTMLElement.TABLE;
    }

    private boolean isListLine(HTMLElement line) {
        return line == HTMLElement.UL ||
               line == HTMLElement.UL2 ||
               line == HTMLElement.UL3 ||
               line == HTMLElement.UL4 ||
               line == HTMLElement.OL;
    }

    private boolean isTableElement(HTMLElement line) {
        return line == HTMLElement.TABLE ||
               line == HTMLElement.TH ||
               line == HTMLElement.TD;
    }

    private DiagramType isDiagramDeclaration(String token) {
        for (DiagramType type : Diagram.TYPES) {
            for (int i=0; i<type.getDefinitions().length; i++) {
                if (token.equals("_" + type.getDefinitions()[i])) {
                    return type;
                }
            }
        }
        return null;
    }

    private HTMLElement getBlockType(String token) {
        for  (HTMLElement element : Html.BLOCK_ELEMENTS) {
            if (token.equals(element.getSymbol())) {
                return element;
            }
        }

        // Check for ordered lists
        if (token.endsWith(".")) {
            try {
                Integer.parseInt(token.substring(0, token.length()-1));
            } catch (Exception e) {
                return HTMLElement.P;
            }
            return HTMLElement.OL;
        }

        return HTMLElement.P;
    }

    private HTMLElement getInlineOpenType(String token) {
        for (HTMLElement element : Html.INLINE_ELEMENTS) {
            if (token.startsWith(element.getSymbol())) {
                return element;
            }
        }
        return null;
    }

    private HTMLElement getInlineCloseType(String token) {
        for (HTMLElement element : Html.INLINE_ELEMENTS) {
            if (token.endsWith(element.getEndSymbol()) ||
                token.endsWith(element.getEndSymbol() + ".") ||
                token.endsWith(element.getEndSymbol() + ",") ||
                token.endsWith(element.getEndSymbol() + ";")) {
                return element;
            }
        }
        return null;
    }

    private void openTag(HTMLElement tag) {
        tags.push(tag);
        output.append(tag.getTag());
    }

    private void openSelfClosingTag(HTMLElement tag) {
        output.append(tag.getTag());
    }

    private void closeTag(HTMLElement tag) {
        output.append(tag.getCloseTag());
    }

    private void closeLastTag() {
        if (!tags.empty()) {
            closeTag(tags.pop());
        }
    }
}
