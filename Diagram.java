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
