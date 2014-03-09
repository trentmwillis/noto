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

public abstract class Diagram {
    public static final DiagramType[] TYPES = {
        DiagramType.TREE,
        DiagramType.FLOWCHART,
        DiagramType.NETWORK,
        DiagramType.VENN
    };

    protected static int globalID = 0;

    protected int id;
    protected File imgFile;
    protected BufferedImage image;

    public Diagram() {
        id = Diagram.globalID++;
    }

    public String getImagePath() {
        // Returns relative image path
        return imgFile.getPath();
    }

    public void makeImage() {
        draw();

        // Save out .png
        try {
            imgFile = new File(id + ".png");
            ImageIO.write(image, "png", imgFile);
        } catch (IOException exception) {
            System.out.println("Exception: " + exception.toString());
            exception.printStackTrace();
        }
    }

    public abstract void addData(String data); // Adds data to diagram

    protected abstract void draw();   // Creates the actual image
}
