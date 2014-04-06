import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

/*
This class represents tree diagrams. As you could probably guess,
it is primarily accomplished by using a tree data structure.
*/

public class Tree extends Diagram {
    private static final int MAX_COL_WIDTH = Html.PAGE_WIDTH / 4;
    private static final int ROW_HEIGHT = 50;
    private static final int NODE_SPACING = 20;

    private ArrayList<TreeNode> nodes; // Stores the nodes for the tree
    private boolean initialized;       // Tracks if the tree has been initialized
    private TreeNode root;             // Reference to the root node
    private Color color;               // Base color of the diagram
    private String title;              // Title of the chart
    private boolean secondaryStyle;    // Boolean to draw a secondary way
    private ArrayList<ArrayList<TreeNode>> grid;

    // Variables to aid in drawing
    private int top = 0;
    private int indent = 50;
    private int height = 50;
    private int halfHeight = height / 2;
    private int nudge = 10;

    // Constructor
    public Tree() {
        super();

        // Init data members
        nodes = new ArrayList<TreeNode>();
        initialized = false;
        color = Color.BLUE;
        title = "";
    }

    // This method adds data in the form
    // <parent> -> <child-1>, <child-2>, ..., <child-n>
    public void addData(String data) throws BuildException {
        try {
            // Check if diagram is initialized
            if (!initialized) {
                firstData(data);
                return;
            }

            // Set up the scanner for the data
            Scanner dataScanner = new Scanner(data);
            dataScanner.useDelimiter ("->");

            // Read in first value
            String value = dataScanner.next().trim();

            // Check if setting the title or color
            if (value.equals("title")) {
                title = dataScanner.next().trim();
                return;
            } else if (value.equals("color")) {
                String color = dataScanner.next().trim();

                Field field = Class.forName("java.awt.Color").getField(color);
                this.color = (Color)field.get(null);

                return;
            } else if (value.equals("alt-style")) {
                secondaryStyle = true;
                return;
            }

            // Check to make sure the node exists
            TreeNode node = findNode(value);

            // Skip past the '->' and change to comma delimiter
            dataScanner.useDelimiter(" ");
            dataScanner.next();
            dataScanner.useDelimiter(",");

            // Initialize the children nodes
            TreeNode childNode;
            while (dataScanner.hasNext()) {
                // Create a node from the next value
                childNode = new TreeNode(dataScanner.next().trim());

                // Add child to both the parent's list and the master list
                nodes.add(childNode);
                node.addChild(childNode);
            }
        }

        // This will occur when they try to pass a parent node that isn't initialized
        catch (NotFoundException e) {
            throw new BuildException("Diagram #" + id + " (Tree): " + e.toString());
        }

        // This will occur when they give a bad color name
        catch (NoSuchFieldException e) {
            throw new BuildException("Diagram #" + id + " (Tree): Unsupported color choice");
        }

        // This will occur when any other syntax violation occurs
        catch (Exception e) {
            throw new BuildException("Diagram #" + id + " (Tree): syntax error\n" + e.toString());
        }
    }

    // Method to add the first set of data to the Tree
    private void firstData(String data) throws BuildException {
        try {
            // Set up the scanner for the data
            Scanner dataScanner = new Scanner(data);
            dataScanner.useDelimiter("->");

            // Initialize the root node
            TreeNode node = new TreeNode(dataScanner.next().trim());
            root = node;
            nodes.add(node);

            // Skip past the '->' and change to comma delimiter
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

            // Finished with initialization
            initialized = true;
        } catch (Exception e) {
            throw new BuildException(e.toString());
        }
    }

    // Method to check if a node exists or not
    private TreeNode findNode(String value) throws NotFoundException {
        // Do a linear search, cause it isn't sorted or anything
        for (TreeNode node : nodes) {
            if (node.getValue().equals(value)) {
                return node;
            }
        }

        // Node does NOT exist, so throw an exception
        throw new NotFoundException("Node <" + value + "> could not be found.");
    }

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
        top = (title != "") ? 50 : 0;

        // Recursive tree drawing...
        if (secondaryStyle) {
            draw2(g);
        } else {
            drawNode(root, g, 0, 0);
        }   

        // Draw title if it exists
        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, width/2 - g.getFontMetrics().stringWidth(title)/2, height - 40);
        }
    }

    // Method to recursively draw nodes
    private void drawNode(TreeNode node, Graphics2D g, int left, int lastTop) {
        // Calculate width of node
        int width = g.getFontMetrics().stringWidth(node.getValue()) + 20;

        // Draw node
        g.setColor(color);
        g.fillRect(left, top, width, height);

        // Draw connection lines
        g.drawLine(left-indent/2, lastTop+height, left-indent/2, top+halfHeight);  // Middle bottom of parent down
        g.drawLine(left-indent/2, top+halfHeight, left, top+halfHeight);  // Side middle of node out

        g.setColor(Color.WHITE);
        g.drawString(node.getValue(), left + nudge, top + halfHeight);

        // Save the position of this node to draw connecting lines
        lastTop = top;

        // Bump the top variable up
        top += height + nudge;

        color = color.darker();

        // Iterate through the nodes children
        for (int i=0; i<node.getChildren().size(); i++) {
            drawNode(node.getChildren().get(i), g, left + indent, lastTop);
        }

        color = color.brighter();
    }

    // TODO
    private void draw2(Graphics2D g) {
        // Construct grid
        grid = new ArrayList<ArrayList<TreeNode>>();
        addNodeToGrid(root, 0);

        drawGrid(g);
    }

    private void addNodeToGrid(TreeNode node, int rowNum) {
        if (node.inGrid) {
            return;
        }

        if (rowNum >= grid.size()) {
            grid.add(new ArrayList<TreeNode>());
        }

        grid.get(rowNum).add(node);
        node.inGrid = true;
        if (node.hasChildren()) {
            for (int i=0; i<node.getChildren().size(); i++) {
                addNodeToGrid(node.getChildren().get(i), rowNum + 1);
            }
        }
    }

    private void drawGrid(Graphics2D g) {
        for (int i=grid.size()-1; i>=0; i--) {
            drawRow(grid.get(i), i*(Tree.ROW_HEIGHT + Tree.NODE_SPACING), g);
        }
    }

    private void drawRow(ArrayList<TreeNode> row, int topPosition, Graphics2D g) {
        int height = Tree.ROW_HEIGHT;
        int width = Math.min(Html.PAGE_WIDTH / row.size(), Tree.MAX_COL_WIDTH);
        int pageMiddle = Html.PAGE_WIDTH / 2;

        for (int i=0; i<row.size(); i++) {
            TreeNode node = row.get(i);

            int leftOffset = (width + Tree.NODE_SPACING) * (i-row.size()/2);
            leftOffset -= (row.size()%2 == 0) ? 0 : (width+Tree.NODE_SPACING)/2;

            node.x = pageMiddle + leftOffset;
            node.y = topPosition;

            // Draw connection lines
            for (int j=0; j<node.getChildren().size(); j++) {
                TreeNode child = node.getChildren().get(j);
                g.drawLine(node.x + width/2, node.y + height, child.x, child.y);
            }

            g.fillRoundRect(node.x, node.y, width, height, 4, 4);
            g.setColor(Color.WHITE);
            g.drawString(node.getValue(), node.x + width/2 - g.getFontMetrics().stringWidth(node.getValue())/2, node.y + height/2);

            color = color.darker();
            g.setColor(color);
        }
    }
}

// Class to represent the nodes in the Tree.
// Really simple and easy to understand class.
class TreeNode {
    private ArrayList<TreeNode> children;
    private String value;
    public int x, y;
    public boolean inGrid;

    public TreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<TreeNode>();
        this.inGrid = false;
    }

    public String getValue() {
        return value;
    }

    public void addChild(TreeNode node) {
        children.add(node);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }
}
