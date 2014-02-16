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

    public abstract String toString();
}

class TreeNode {
    private String value;
    private ArrayList<TreeNode> children;

    public TreeNode(String value) {
        this.value = value;
        children = new ArrayList<TreeNode>();

        System.out.println("New node " + value);
    }

    public String getValue() {
        return value;
    }

    public void addChild(TreeNode node) {
        children.add(node);
    }
}

class Tree extends Diagram {
    ArrayList<TreeNode> nodes;

    public Tree() {
        nodes = new ArrayList();
    }

    public void addData(String data) {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter("->");

        TreeNode node = new TreeNode(dataScanner.next());

        nodes.add(node);

        dataScanner.useDelimiter(",");

        TreeNode childNode;
        while (dataScanner.hasNext()) {
            childNode = new TreeNode(dataScanner.next());
            nodes.add(childNode);
            node.addChild(childNode);
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i=0; i<nodes.size(); i++) {
            result.append(nodes.get(i).getValue());
        }
        return result.toString();
    }

    public void draw() {
        System.out.println("Drawing...");
        return;
    }
}
