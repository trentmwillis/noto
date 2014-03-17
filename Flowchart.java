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
    private ArrayList<FlowNode> nodes;
    private FlowNode start;
    private boolean initialized;
    private String title;
    private Color color;


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
                start = new FlowNode(value);
                nodes.add(start);
                lastNode = start;
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

    protected void draw() {
        // Width is equal to the widest an image can be
        int width = Html.PAGE_WIDTH;
        int height = (nodes.size() + 1) * 100;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(color);

        // Recursive flowchart drawing...
        drawNode(start, g, 0, 0);
    }

    private void drawNode(FlowNode node, Graphics2D g, int x, int y) {
        // Calculate width of node
        int nudgeX = 10;
        int nudgeY = 20;

        int width = g.getFontMetrics().stringWidth(node.getValue()) + 20;
        int height = 50;

        int columnWidth = width + 30;
        int rowHeight = height + 30;

        // Draw connection lines
        // g.drawLine(left-indent, lastTop, left-indent, top+halfHeight);
        // g.drawLine(left-indent, top+halfHeight, left, top+halfHeight);

        // Iterate through the nodes children
        int childX = x + columnWidth;
        int childY = y;
        int triX[] = new int[3];
        int triY[] = new int[3];

        node.draw();

        for (int i=0; i<node.getChildren().size(); i++) {
            if (!node.getChildren().get(i).isDrawn()) {
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
        }

        // Draw node;
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawString(node.getValue(), x + nudgeX, y + nudgeY);
        g.setColor(color);
    }
}

class FlowNode {
    private String value;
    private ArrayList<FlowNode> children;
    private ArrayList<FlowNode> parents;
    private boolean drawn;

    public FlowNode(String value) {
        this.value = value;
        this.children = new ArrayList<FlowNode>();
        this.parents = new ArrayList<FlowNode>();
        this.drawn = false;
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

    public boolean isDrawn() {
        return drawn;
    }

    public void draw() {
        drawn = true;
    }
}
