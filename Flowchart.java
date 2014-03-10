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
    private ArrayList<FlowNode> nodes;
    private FlowNode start;
    private boolean initialized;


    public Flowchart() {
        super();
        nodes = new ArrayList<FlowNode>();
        initialized = false;
    }

    public void addData(String data) {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter ("->");

        FlowNode node;
        String value;
        FlowNode lastNode = null;

        if (!initialized) {
            start = new FlowNode(dataScanner.next().trim());
            nodes.add(start);
            lastNode = start;
            initialized = true;
        }

        while (dataScanner.hasNext()) {
            value = dataScanner.next().trim();

            node = findNode(value);

            if (node == null) {
                node = new FlowNode(value);
                nodes.add(node);
            }

            if (lastNode != null) {
                lastNode.addChild(node);
                node.addParent(lastNode);
            }

            lastNode = node;
        }
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

    protected void draw() {
        // Width is equal to the widest an image can be
        int width = Html.PAGE_WIDTH;
        // Set height equal to the maximum depth level * a constant
        int height = (nodes.size() + 1) * 100;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(Color.BLACK);

        // Recursive flowchart drawing...
        drawNode(start, g, 0, 0);
    }

    private void drawNode(FlowNode node, Graphics g, int x, int y) {
        // Calculate width of node
        int nudgeX = 10;
        int nudgeY = 20;

        int columnWidth = 100;
        int rowHeight = 100;

        int width = g.getFontMetrics().stringWidth(node.getValue()) + 20;
        int height = 50;

        // Draw connection lines
        // g.drawLine(left-indent, lastTop, left-indent, top+halfHeight);
        // g.drawLine(left-indent, top+halfHeight, left, top+halfHeight);

        // Iterate through the nodes children
        int childX = x + columnWidth;
        int childY = y;
        int triX[] = new int[3];
        int triY[] = new int[3];
        for (int i=0; i<node.getChildren().size(); i++) {
            drawNode(node.getChildren().get(i), g, childX, childY);
            g.drawLine(x, y + height/2, childX-nudgeX, childY+height/2);

            triX[0] = childX-nudgeX;
            triX[1] = childX-nudgeX;
            triX[2] = childX;

            triY[0] = childY+(height/2)-5;
            triY[1] = childY+(height/2)+5;
            triY[2] = childY+(height/2);

            g.fillPolygon(triX,triY,3);
            childY += rowHeight;
        }

        // Draw node
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        g.drawString(node.getValue(), x + nudgeX, y + nudgeY);
    }
}

class FlowNode {
    private String value;
    private ArrayList<FlowNode> children;
    private ArrayList<FlowNode> parents;

    public FlowNode(String value) {
        this.value = value;
        this.children = new ArrayList<FlowNode>();
        this.parents = new ArrayList<FlowNode>();
    }

    public String getValue() {
        return value;
    }

    public void addChild(FlowNode node) {
        children.add(node);
    }

    public void addParent(FlowNode node) {
        parents.add(node);
    }

    public ArrayList<FlowNode> getChildren() {
        return children;
    }

    public ArrayList<FlowNode> getParents() {
        return parents;
    }
}
