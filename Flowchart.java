import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

/*
This class represents flowcharts.
*/

public class Flowchart extends Diagram {
    private static final int MAX_COL_WIDTH = Html.PAGE_WIDTH / 4;
    private static final int ROW_HEIGHT = 50;
    private static final int NODE_SPACING = 20;

    private ArrayList<FlowNode> nodes;
    private FlowNode root;
    private boolean initialized;
    private String title;
    private Color color;
    private ArrayList<ArrayList<FlowNode>> grid;


    public Flowchart() {
        super();
        nodes = new ArrayList<FlowNode>();
        initialized = false;
        title = "";
        color = Color.BLUE;
    }

    public void addData(String data) throws BuildException {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter ("->");

        try {
            FlowNode node;
            String value = dataScanner.next().trim();
            FlowNode lastNode = null;

            // Check if setting the title or color
            if (value.equals("title")) {
                title = dataScanner.next().trim();
                return;
            } else if (value.equals("color")) {
                String color = dataScanner.next().trim();

                Field field = Class.forName("java.awt.Color").getField(color);
                this.color = (Color)field.get(null);

                return;
            }

            if (!initialized) {
                root = new FlowNode(value);
                nodes.add(root);
                lastNode = root;
                initialized = true;
            } else {
                lastNode = findNode(value);
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

        // This will occur when they give a bad color name
        catch (NoSuchFieldException e) {
            throw new BuildException("FLOWCHART: Unsupported color choice");
        }

        // This will occur when any other syntax violation occurs
        catch (Exception e) {
            throw new BuildException("FLOWCHART: syntax error\n" + e.toString());
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
        return maxDepth(root);
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

    protected void draw() {
        // Create a grid of all the nodes
        constructGrid();

        // Width is equal to the widest an image can be
        int width = Html.PAGE_WIDTH;
        int height = grid.size() * (Flowchart.ROW_HEIGHT + Flowchart.NODE_SPACING);
        height += (title != "") ? 100 : 0;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(color);

        // Position all the nodes and draw them
        drawGrid(g);

        // If there is a title, draw it
        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, width/2 - g.getFontMetrics().stringWidth(title)/2, height - 40);
        }
    }

    private void constructGrid() {
        grid = new ArrayList<ArrayList<FlowNode>>();
        addNodeToGrid(root, 0);
    }

    private void addNodeToGrid(FlowNode node, int rowNum) {
        if (node.inGrid) {
            return;
        }

        if (rowNum >= grid.size()) {
            grid.add(new ArrayList<FlowNode>());
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
        for (int i=0; i<grid.size(); i++) {
            positionRow(grid.get(i), (Flowchart.ROW_HEIGHT + Flowchart.NODE_SPACING) * i);
        }

        for (int i=0; i<grid.size(); i++) {
            drawRow(grid.get(i), g);
        }
    }

    private void positionRow(ArrayList<FlowNode> row, int topPosition) {
        int height = Flowchart.ROW_HEIGHT;
        int width = Math.min(Html.PAGE_WIDTH / row.size(), Flowchart.MAX_COL_WIDTH);
        int pageMiddle = Html.PAGE_WIDTH / 2;

        for (int i=0; i<row.size(); i++) {
            FlowNode node = row.get(i);

            int leftOffset = (width+Flowchart.NODE_SPACING) * (i-row.size()/2);
            leftOffset -= (row.size()%2 == 0) ? 0 : (width+Flowchart.NODE_SPACING)/2;

            node.x = pageMiddle + leftOffset;
            node.y = topPosition;
            node.height = Flowchart.ROW_HEIGHT;
            node.width = width;
        }
    }

    private void drawRow(ArrayList<FlowNode> row, Graphics2D g) {
        for (int i=0; i<row.size(); i++) {
            FlowNode node = row.get(i);

            drawConnections(node, g);
            g.fillRoundRect(node.x, node.y, node.width, node.height, 4, 4);

            g.setColor(Color.WHITE);
            g.drawString(node.getValue(), node.x + node.width/2 - g.getFontMetrics().stringWidth(node.getValue())/2, node.y + node.height/2);

            color = color.darker();
            g.setColor(color);
        }
    }

    private void drawConnections(FlowNode node, Graphics2D g) {
        int triSize = 10;
        int triX[] = new int[3];
        int triY[] = new int[3];
        int strokeWidth = 2;
        ArrayList<FlowNode> children = node.getChildren();

        g.setStroke(new BasicStroke(strokeWidth));
        for (int i=0; i<children.size(); i++) {
            int childX = children.get(i).x + children.get(i).width/2;
            int childY = children.get(i).y;

            g.drawLine(node.x+strokeWidth, node.y+strokeWidth, childX, childY-triSize);

            triX[0] = childX-triSize/2;
            triX[1] = childX+triSize/2;
            triX[2] = childX;

            triY[0] = childY-triSize;
            triY[1] = childY-triSize;
            triY[2] = childY;

            g.fillPolygon(triX,triY,3);
        }
        g.setStroke(new BasicStroke(1));
    }
}

class FlowNode {
    private String value;
    private ArrayList<FlowNode> children;
    private ArrayList<FlowNode> parents;
    public int x, y, width, height;
    public boolean inGrid;

    public FlowNode(String value) {
        this.value = value;
        this.children = new ArrayList<FlowNode>();
        this.parents = new ArrayList<FlowNode>();
        this.inGrid = false;
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

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public ArrayList<FlowNode> getChildren() {
        return children;
    }

    public ArrayList<FlowNode> getParents() {
        return parents;
    }
}
