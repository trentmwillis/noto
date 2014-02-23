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

public class Tree extends Diagram {
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

        // Recursive tree drawing...
        drawNode(root, g, 0, 0);
    }

    private void drawNode(TreeNode node, Graphics g, int left, int lastTop) {
        // Calculate width of node
        int width = g.getFontMetrics().stringWidth(node.getValue()) + 20;

        // Draw node
        g.drawString(node.getValue(), left + nudge, top + halfHeight);
        g.drawRect(left, top, width, height);

        // Draw connection lines
        g.drawLine(left-indent, lastTop, left-indent, top+halfHeight);
        g.drawLine(left-indent, top+halfHeight, left, top+halfHeight);

        // Save the position of this node to draw connecting lines
        lastTop = top;
        // Bump the top variable up
        top += height + nudge;

        // Iterate through the nodes children
        for (int i=0; i<node.getChildren().size(); i++) {
            drawNode(node.getChildren().get(i), g, left + indent, lastTop);
        }
    }
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
