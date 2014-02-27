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

public class Venn extends Diagram {
    private ArrayList<String> left;
    private ArrayList<String> right;
    private ArrayList<String> middle;
    private int onSide;

    private static final int NO_SIDE     = 0;
    private static final int LEFT_SIDE   = 1;
    private static final int RIGHT_SIDE  = 2;
    private static final int MIDDLE_SIDE = 3;


    public Venn() {
        super();
        left = new ArrayList<String>();
        right = new ArrayList<String>();
        middle = new ArrayList<String>();
        int onSide = NO_SIDE;
    }

    public void addData(String data) {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter(":");

        // On a starting line for one of the parts
        if (dataScanner.hasNext()) {
            onSide = Interger.parseInt(dataScanner.next().trim());
        } else {
            dataScanner.reset();

            String value = dataScanner.nextLine().trim();

            switch (onSide) {
                case LEFT_SIDE   : left.add(value);
                                   break;
                case RIGHT_SIDE  : right.add(value);
                                   break;
                case MIDDLE_SIDE : middle.add(value);
                                   break;
                default          : System.err.println("Error: invalid Venn syntax");
            }
        }
    }

    /* Drawing Methods */

    private int top = 0;
    private int indent = 50;
    private int height = 50;
    private int halfHeight = height / 2;
    private int nudge = 10;

    protected void draw() {
        // Width is equal to the widest an image can be
        int width = Html.PAGE_WIDTH;
        // Set height equal to the maximum depth level * a constant
        int height = (nodes.size() + 1) * 60;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(Color.BLACK);

        // Reset the 'top' variable
        top = 0;

        NetworkNode node;
        for (int i=0; i<nodes.size(); i++) {
            node = nodes.get(i);
            node.x = 0;
            node.y = 60 * i;
        }

        nodes.get(0).draw(g);
    }
}
