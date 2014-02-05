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

    private String overhead = "<html><head><meta charset='utf-8'><title>Noto Note</title><link rel='stylesheet' href='styles.css'><link rel='stylesheet' href='http://yandex.st/highlightjs/8.0/styles/default.min.css'></head><body><div id='content'>";

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

                writer.append("</div><script src='http://yandex.st/highlightjs/8.0/highlight.min.js'></script><script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js'></script><script>$(document).ready(function() {$('pre').each(function(i, e) {hljs.highlightBlock(e)});});</script></body>");
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
