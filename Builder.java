import java.io.*;
import java.util.Scanner;

/*
Builder class represents the Facade through which the user can call the building
of files. It is a singleton class because there should never be more than one.
*/

public class Builder {
    private static Builder instance;

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

    public void buildSingle(boolean single) {
        // Create the output folders, stylesheets, and TODO: SCRIPTS
        createOutputDirs();
        createStylesheets();
        createScripts();

        // Build the file
        buildFile(ProjectManager.getInstance().getCurrentFile(), single);
    }

    public void buildAll() {
        // Get the list of files from the project directory
        File[] files = new File(ProjectManager.getInstance().getProjectPath()).listFiles();

        // Create the output folders, stylesheets, and TODO: SCRIPTS
        createOutputDirs();
        createStylesheets();
        createScripts();

        // Loop through and build each file
        for (int i=0; i<files.length; i++) {
            String name = files[i].getName();

            // Check to make sure it is a file (not a folder) and a .txt file
            if (files[i].isFile() && name.substring(name.lastIndexOf('.')+1).equals("txt")) {
                // Build file
                ProjectManager.getInstance().setCurrentFile(files[i]);
                buildFile(files[i], false);
            }
        }
    }

    private void buildFile(File input, boolean single) {
        try {
            // Get the name of the file
            String name = (single) ? 
                ProjectManager.getInstance().getFileName() : 
                input.getName().substring(0, input.getName().lastIndexOf('.'));

            System.out.println("Building file: " + name);

            // See if the file exists, or create a new one
            File htmlFile = new File(ProjectManager.getInstance().getOutputPath(), name + ".html");
            if (!htmlFile.exists()) {
                htmlFile.createNewFile();
            }

            // Create a writer to the HTML file
            FileWriter writer = new FileWriter(htmlFile);

            // Write the opening information
            writer.write(Html.head(name));

            // Write the navigation info
            if (!single) {
                writer.write(Html.navigation(input.getParent(), name));
            }

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

    private void createStylesheets() {
        copyFile("css/styles.min.css", ProjectManager.getInstance().getCSSPath() + "styles.min.css");
        copyFile("css/highlight.min.css", ProjectManager.getInstance().getCSSPath() + "highlight.min.css");
    }

    private void createScripts() {
        copyFile("scripts/jquery.min.js", ProjectManager.getInstance().getScriptPath() + "jquery.min.js");
        copyFile("scripts/highlight.min.js", ProjectManager.getInstance().getScriptPath() + "highlight.min.js");
    }

    private void copyFile(String source, String destination) {
        try {
            // Create the new file
            File newFile = new File(destination);
            if (newFile.exists()) {
                return;
            }
            newFile.createNewFile();

            // Load the source file
            File srcFile = new File(source);

            // Write source to the new file
            FileWriter writer = new FileWriter(newFile);
            Scanner srcScanner = new Scanner(srcFile);
            while (srcScanner.hasNextLine()) {
                writer.append(srcScanner.nextLine());
            }
            writer.flush();
            writer.close();
        }

        // Catch any exceptions
        catch (IOException e) {
            System.out.println("Error copying file: " + e.toString());
        }
    }

    private void createOutputDirs() {
        createDir(ProjectManager.getInstance().getOutputPath());
        createDir(ProjectManager.getInstance().getImagePath());
        createDir(ProjectManager.getInstance().getCSSPath());
        createDir(ProjectManager.getInstance().getScriptPath());
    }

    private void createDir(String dir) {
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
}
