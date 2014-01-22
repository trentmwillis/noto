import java.io.File;
import java.util.Stack;     // Used in Interpreter class

public class Noto {
	public static void main(String[] args) {
		System.out.println("Hooray! Initial setup complete");
	}
}

class Builder {
    private static Parser parser;
    private static Interpreter interpreter;
    private static File output;
    private static File input;

    public static void build(File input) {

    }

    public static void buildAll() {

    }
}

class Interpreter {
    // Markov algorithm
    private Stack<String> tags;
    private String output;

    public static void interpret(String line) {
        // Check beginning of each word for tags
        // Should I use RegEx? Probably..
    }

    public static String getOutput() {
        // Closes open tags and returns output
    }
}

class Parser {
    private Diagram building;

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

abstract class Diagram {
    private File imgFile;
    private Image img;

    public getImagePath() {
        // Returns relative image path
        return imgFile.getPath();
    }

    public void makeImage() {
        draw();

        // Save out .png
    }

    public abstract void addData(String data); // Adds data to diagram

    private abstract void draw();   // Creates the actual image
}
