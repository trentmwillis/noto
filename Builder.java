import java.io.*;
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

    protected Builder() { /* Nothing */ }

    public static Builder getInstance() {
        if (instance == null) {
            instance = new Builder();
        }
        return instance;
    }

    public void build(File input) {
        if (input != null) {
            try {
                // Create HTML file
                String[] nameParts = input.getName().split("\\.");
                System.out.println("Building file: " + nameParts[0]);
                File htmlFile = new File(nameParts[0] + ".html");
                htmlFile.createNewFile();

                // Create a writer to the HTML file
                FileWriter writer = new FileWriter(htmlFile);

                // Write the opening information
                writer.write(Html.HEAD);

                // Reset the Interpreter from any prior builds
                Interpreter.getInstance().reset();

                // Loop through each line in the input File and interpret the input to HTML
                Scanner fileScanner = new Scanner(input);
                while (fileScanner.hasNextLine()) {
                    Interpreter.getInstance().interpret(fileScanner.nextLine());
                }

                // Write the interpreted information to the document
                writer.append(Interpreter.getInstance().getOutput());

                // Write the closing information
                writer.append(Html.END);

                // Flush and close the write stream
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
