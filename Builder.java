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

    public void buildAll(String directory) {
        File folder = new File(directory);
        File[] files = folder.listFiles();

        // Loop through and build each file
        for (int i=0; i<files.length; i++) {
            int index = files[i].getName().lastIndexOf('.');

            // Check to make sure it is a .txt file
            if (files[i].isFile() && files[i].getName().substring(index+1).equals("txt")) {
                // Build file
                buildProjectFile(files[i]);
            }
        }

        createStylesheet(directory);
    }

    private void buildProjectFile(File input) {
        try {
            int index = input.getName().lastIndexOf('.');
            String name = input.getName().substring(0, index);

            System.out.println("Building file in project: " + name);

            // See if the file exists, or create a new one
            File htmlFile = new File(input.getParent(), name + ".html");
            if (!htmlFile.exists()) {
                htmlFile.createNewFile();
            }

            // Create a writer to the HTML file
            FileWriter writer = new FileWriter(htmlFile);

            // Write the opening information
            writer.write(Html.head(name));

            // Write the navigation info
            writer.write(Html.navigation(input.getParent(), name));

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

    private void createStylesheet(String directory) {
        System.out.println("Creating stylesheet...");

        try {
            // Make sure the "css" folder exists
            File folder = new File(directory + "/css");
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Make sure the "styles" file exists
            File stylesheet = new File(folder.getPath() + "/styles.css");
            if (!stylesheet.exists()) {
                
                stylesheet.createNewFile();
            }

            // Write stylesheet to the file
            FileWriter writer = new FileWriter(stylesheet);
            writer.write(Html.CSS);
            writer.flush();
            writer.close();
        }

        // Catch any exceptions
        catch (IOException e) {
            System.out.println("Error creating stylesheet: " + e.toString());
        }
    }
}
