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

public class Flowchart extends Diagram {
    private boolean initialized;
    private ArrayList<FlowNode> nodes;
    private FlowNode start;


    public Flowchart() {
        super();
        nodes = new ArrayList<FlowNode>();
        initialized = false;
    }

    public void addData(String data) {
        // See if this is the first data being input to the Flowchart
        if (!initialized) {
            firstData(data);
            return;
        }

        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter ("->");
        FlowNode node = findNode(dataScanner.next().trim());

        if (node != null) {

            // Skip past the '->'
            dataScanner.useDelimiter(" ");
            dataScanner.next();

            dataScanner.useDelimiter(",");

            // Initialize the children nodes
            FlowNode childNode;
            while (dataScanner.hasNext()) {
                childNode = new FlowNode(dataScanner.next().trim());
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

        // Initialized the start node
        FlowNode node = new FlowNode(dataScanner.next().trim());
        start = node;
        nodes.add(node);

        // Skip past the '->'
        dataScanner.useDelimiter(" ");
        dataScanner.next();

        dataScanner.useDelimiter(",");

        // Initialize the children nodes
        FlowNode childNode;
        while (dataScanner.hasNext()) {
            childNode = new FlowNode(dataScanner.next().trim());
            nodes.add(childNode);
            node.addChild(childNode);
        }

        initialized = true;
    }

    private FlowNode findNode(String value) {
        for (FlowNode node : nodes) {
            if (node.getValue().equals(value)) {
                return node;
            }
        }
        return null;
    }

    private int maxDepth() {
        return maxDepth(start);
    }

    private int maxDepth(FlowNode node) {
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
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(Color.BLACK);

        // Reset the 'top' variable
        top = 0;

        // Recursive Flowchart drawing...
        drawNode(start, g, 0, 0);
    }

    private void drawNode(FlowNode node, Graphics g, int left, int lastTop) {
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

class FlowNode {
    private String value;
    private ArrayList<FlowNode> children;

    public FlowNode(String value) {
        this.value = value;
        children = new ArrayList<FlowNode>();
    }

    public String getValue() {
        return value;
    }

    public void addChild(FlowNode node) {
        children.add(node);
    }

    public ArrayList<FlowNode> getChildren() {
        return children;
    }
}
