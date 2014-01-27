import java.io.File;
import java.util.Stack;     // Used in Interpreter class

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.lang.Math;
import java.util.Random;
import java.util.ArrayList;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.SwingUtilities;

public class Noto {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 640;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

    public static void createAndShowGUI() {
        JFrame frame = new NotoFrame(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(true);
    }
}

class NotoFrame extends JFrame {
    private final int WIDTH;
    private final int HEIGHT;

    private JFileChooser fileChooser = new JFileChooser();

    private JTextArea textArea = new JTextArea();

    public NotoFrame(int width, int height) {
        setTitle("Noto - Note-Taking App");
        WIDTH = width;
        HEIGHT = height;
        setSize(width, height);

        initMenu();
        initTextArea();
    }

    private void initTextArea() {
        textArea.setLineWrap(true);
        textArea.setMargin(new Insets(12,6,12,6));
        textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        setContentPane(new JScrollPane(textArea));
    }

    private void initMenu() {
        JMenu menu = new JMenu("File");

        JMenuItem item = new JMenuItem("Save");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // Function to save
                save();
            }
        });
        menu.add(item);

        item = new JMenuItem("Load");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // Function to load
                load();
            }
        });
        menu.add(item);

        item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               System.exit(0);
            }
        });
        menu.add(item);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        menu = new JMenu("Build");

        item = new JMenuItem("Build Single");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // Function to build current document
                save();
                Builder.getInstance().build(fileChooser.getSelectedFile());
            }
        });
        menu.add(item);

        item = new JMenuItem("Build Project");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               // Function to build all in project
            }
        });
        menu.add(item);

        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    private void save() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                textArea.write(writer);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            
        }
    }

    private void load() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                FileReader reader = new FileReader(file);
                textArea.read(reader, file);
            } catch (FileNotFoundException e) {
                System.out.println(e.toString());
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
}

/*
Builder class represents the Facade through which the user can call the building
of files. It is a singleton class because there should never be more than one.
*/
class Builder {
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
        System.out.println(input.getName());
    }

    public void buildAll() {

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
        return "";
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

abstract class Diagram {
    private File imgFile;
    private Image img;

    public String getImagePath() {
        // Returns relative image path
        return imgFile.getPath();
    }

    public void makeImage() {
        draw();

        // Save out .png
    }

    public abstract void addData(String data); // Adds data to diagram

    abstract void draw();   // Creates the actual image
}
