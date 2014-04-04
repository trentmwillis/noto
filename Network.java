import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

public class Network extends Diagram {
    private ArrayList<NetworkNode> nodes;
    private String title;
    private Color color;


    public Network() {
        super();
        nodes = new ArrayList<NetworkNode>();
        title = "";
        color = Color.BLUE;
    }

    public void addData(String data) throws BuildException {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter("--");
        String value = dataScanner.next().trim();

        try {
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

            NetworkNode rootNode;

            if ((rootNode = findNode(value)) == null) {
                rootNode = new NetworkNode(value);
                nodes.add(rootNode);
            }

            NetworkNode otherNode;
            while (dataScanner.hasNext()) {
                value = dataScanner.next().trim();

                if ((otherNode = findNode(value)) == null) {
                    otherNode = new NetworkNode(value);
                    nodes.add(otherNode);
                }

                otherNode.addConnection(rootNode);
                rootNode.addConnection(otherNode);
            }
        }

        // This will occur when they give a bad color name
        catch (NoSuchFieldException e) {
            throw new BuildException("NETWORK DIAGRAM: Unsupported color choice");
        }

        // This will occur when any other syntax violation occurs
        catch (Exception e) {
            throw new BuildException("NETWORK DIAGRAM: syntax error\n" + e.toString());
        }
    }

    private NetworkNode findNode(String value) {
        for (NetworkNode node : nodes) {
            if (node.getValue().equals(value)) {
                return node;
            }
        }
        return null;
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
        int height = (nodes.size()/2 + 1) * (NetworkNode.NODE_HEIGHT + NetworkNode.NODE_GAP);

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

        // Reset the 'top' variable
        top = 0;

        NetworkNode node, lastNode = null;
        int cx = width/2;
        int cy = (title != "") ? (height-100)/2 : height/2;
        double theta = 0;
        double r = cy;
        double dTheta = 360/nodes.size();
        for (int i=0; i<nodes.size(); i++) {
            node = nodes.get(i);
            node.initDimensions(g);
            node.x = (int)Math.round(r * Math.cos(Math.toRadians(theta))) + cx - node.getWidth()/2;
            node.y = (int)Math.round(r * Math.sin(Math.toRadians(theta))) + cy;
            // if (lastNode != null && Math.abs(node.y - lastNode.y) < lastNode.getHeight()) {
            //     node.y = lastNode.y - lastNode.getHeight() - 10;
            // }
            theta += dTheta;
            lastNode = node;
        }

        nodes.get(0).draw(g);

        // Draw the title if set
        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, width/2 - g.getFontMetrics().stringWidth(title)/2, height - 40);
        }
    }
}

class NetworkNode {
    public static final int NODE_HEIGHT = 50;
    public static final int NODE_GAP = 20;

    private String value;
    private ArrayList<NetworkNode> connections;
    private boolean drawn;
    private int width, height;
    public int x, y;

    public NetworkNode(String value) {
        this.value = value;
        this.drawn = false;
        this.connections = new ArrayList<NetworkNode>();
    }

    public String getValue() {
        return value;
    }

    public void addConnection(NetworkNode node) {
        connections.add(node);
    }

    public ArrayList<NetworkNode> getConnections() {
        return connections;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void initDimensions(Graphics2D g) {
        width = g.getFontMetrics().stringWidth(value) + NetworkNode.NODE_GAP;
        height = NetworkNode.NODE_HEIGHT;
    }

    public void draw(Graphics2D g) {
        drawn = true;

        int offset = Html.PAGE_WIDTH / connections.size();

        // Draw connection lines
        for (int i=0; i<connections.size(); i++) {
            NetworkNode node = connections.get(i);
            if (!node.isDrawn()) {
                node.draw(g);
            }

            g.drawLine(x+width/2, y+height/2, node.x+node.getWidth()/2, node.y+node.getHeight()/2);
        }

        g.fillRoundRect(x, y, width, height, 4, 4);
        Color color = g.getColor();
        g.setColor(Color.BLACK);
        g.drawString(value, x + 10, y + 20);
        g.setColor(color);
    }
}
