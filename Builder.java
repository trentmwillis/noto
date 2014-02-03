import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.lang.Math;
import java.util.Random;
import java.util.ArrayList;
import java.util.Stack;     // Used in Interpreter class
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.SwingUtilities;
import javax.swing.undo.*;

import java.util.Scanner;

/*
Builder class represents the Facade through which the user can call the building
of files. It is a singleton class because there should never be more than one.
*/
public class Builder {
    private static Builder instance;

    private Parser parser;
    private Interpreter interpreter;
    private File output;
    private File input;

    private String overhead = "<html><head><meta charset='utf-8'><title>Noto Note</title></head><body>";

    protected Builder() { /* Nothing */ }

    public static Builder getInstance() {
        if (instance == null) {
            instance = new Builder();
        }
        return instance;
    }

    public void build(File input) {
        if (input != null) {
            String[] nameParts = input.getName().split("\\.");
            System.out.println("Building file: " + nameParts[0]);
            File htmlFile = new File(nameParts[0] + ".html");

            try {
                htmlFile.createNewFile();
                FileWriter writer = new FileWriter(htmlFile);
                writer.write(overhead);

                Interpreter.getInstance().reset();

                // Loop through each line in the File
                Scanner fileScanner = new Scanner(input);
                while (fileScanner.hasNextLine()) {
                    Interpreter.getInstance().interpret(fileScanner.nextLine());
                }

                writer.append(Interpreter.getInstance().getOutput());

                writer.append("</body>");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.err.println("Build error: " + e.toString());
            }
        }
    }

    public void buildAll() {

    }
}

class Interpreter {
    private static Interpreter instance;

    private static Stack<String> tags = new Stack<String>();
    private static StringBuilder output;
    private static Scanner scanner;

    protected Interpreter() { /* Nothing */ }

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

    public static void interpret(String line) {
        // Check beginning of each word for tags
        scanner = new Scanner(line);

        // Check beginning of line for block style
        // Default to 'p'
        // Headings, pre, blockquote, hr
        if (scanner.hasNext()) {
            String start = scanner.next();
            HTMLElement type = getBlockType(start);
            openTag(type.getTag());

            // Loop through each token
            while (scanner.hasNext()) {
                output.append(scanner.next());
            }

            closeTag(tags.pop());
        }
    }

    private static HTMLElement getBlockType(String token) {
        for  (HTMLElement element : HTMLElement.values()) {
            if (token.equals(element.getSymbol())) {
                return element;
            }
        }

        return HTMLElement.P;
    }

    public static String getOutput() {
        // Closes any open tags
        while (!tags.empty()) {
            closeTag(tags.pop());
        }

        // Return output as String
        return output.toString();
    }

    private static void openTag(String tag) {
        tags.push(tag);
        output.append("<" + tag + ">");
    }

    private static void closeTag(String tag) {
        output.append("</" + tag + ">");
    }
}

class Parser {
    private static Diagram building;

    public static void newDiagram(Class type) {
        // Use reflection?
    }

    public static void addData(String data) {
        if (building != null) {
            building.addData(data);     // Add more data to the diagram
        }
    }

    public static String getImage() {
        if (building != null) {
            building.makeImage();           // Saves out a .png of the diagram
            return building.getImagePath(); // Return relative path to diagram
        }

        return "";
    }
}
