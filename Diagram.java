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
        DiagramType.NETWORK
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

class TreeNode {
    private String value;
    private ArrayList<TreeNode> children;

    public TreeNode(String value) {
        this.value = value;
        children = new ArrayList<TreeNode>();
    }

    public String getValue() {
        return value;
    }

    public void addChild(TreeNode node) {
        children.add(node);
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }
}

class Tree extends Diagram {
    private boolean initialized;
    private ArrayList<TreeNode> nodes;
    private TreeNode root;


    public Tree() {
        super();
        nodes = new ArrayList<TreeNode>();
        initialized = false;
    }

    public void addData(String data) {
        // See if this is the first data being input to the tree
        if (!initialized) {
            firstData(data);
            return;
        }

        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter ("->");
        TreeNode node = findNode(dataScanner.next().trim());

        if (node != null) {

            // Skip past the '->'
            dataScanner.useDelimiter(" ");
            dataScanner.next();

            dataScanner.useDelimiter(",");

            // Initialize the children nodes
            TreeNode childNode;
            while (dataScanner.hasNext()) {
                childNode = new TreeNode(dataScanner.next().trim());
                nodes.add(childNode);
                node.addChild(childNode);
            }
        } else {
            System.err.println("Error: could not find node.");
        }
    }

    private void firstData(String data) {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter("->");

        // Initialized the root node
        TreeNode node = new TreeNode(dataScanner.next().trim());
        root = node;
        nodes.add(node);

        // Skip past the '->'
        dataScanner.useDelimiter(" ");
        dataScanner.next();

        dataScanner.useDelimiter(",");

        // Initialize the children nodes
        TreeNode childNode;
        while (dataScanner.hasNext()) {
            childNode = new TreeNode(dataScanner.next().trim());
            nodes.add(childNode);
            node.addChild(childNode);
        }

        initialized = true;
    }

    private TreeNode findNode(String value) {
        for (TreeNode node : nodes) {
            if (node.getValue().equals(value)) {
                return node;
            }
        }
        return null;
    }

    private int maxDepth() {
        return maxDepth(root);
    }

    private int maxDepth(TreeNode node) {
        int max = 0;
        for (int i=0; i<node.getChildren().size(); i++) {
            int depth = maxDepth(node.getChildren().get(i));
            if (max < depth) {
                max = depth;
            }
        }
        return ++max;
    }

    protected void draw() {
        // Width is equal to the widest an image can be
        int width = Html.PAGE_WIDTH;
        // Set height equal to the maximum depth level * a constant
        int height = nodes.size() * 50;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(Color.BLACK);

        // Recursive tree drawing...
        drawNode(root, g, 0);
    }

    private int top = 0;

    private void drawNode(TreeNode node, Graphics g, int left) {
        // Calculate width of node
        int width = g.getFontMetrics().stringWidth(node.getValue()) + 20;

        // Draw node
        g.drawString(node.getValue(), left + 5, top + 25);
        g.drawOval(left, top, width, 50);

        // Draw connection lines
        g.drawLine(left-50, top-25, left-50, top+25);
        g.drawLine(left-50, top+25, left, top+25);

        top += 50;

        // Iterate through the nodes children
        for (int i=0; i<node.getChildren().size(); i++) {
            drawNode(node.getChildren().get(i), g, left + 50);
        }
    }
}
