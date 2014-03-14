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
            throw new BuildException("VENN DIAGRAM: Unsupported color choice");
        }

        // This will occur when any other syntax violation occurs
        catch (Exception e) {
            throw new BuildException("PIE DIAGRAM: syntax error\n" + e.toString());
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
        // Set height equal to the maximum depth level * a constant
        int height = (nodes.size() + 1) * 60;
        height = (title != "") ? height + 100 : height;

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

        NetworkNode node;
        for (int i=0; i<nodes.size(); i++) {
            node = nodes.get(i);
            node.x = 0;
            node.y = 60 * i;
        }

        nodes.get(0).draw(g);

        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, width/2 - g.getFontMetrics().stringWidth(title)/2, height - 40);
        }
    }
}

class NetworkNode {
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

    public void draw(Graphics2D g) {
        draw(g, 0);
    }

    public void draw(Graphics2D g, int xOffset) {
        width = g.getFontMetrics().stringWidth(value) + 20;
        height = 50;

        x = (Html.PAGE_WIDTH / 2) - (width / 2) + xOffset;

        g.fillRect(x, y, width, height);
        Color color = g.getColor();
        g.setColor(Color.BLACK);
        g.drawString(value, x + 10, y + height / 2);
        g.setColor(color);

        drawn = true;

        int offset = Html.PAGE_WIDTH / connections.size();

        // Draw connection lines
        for (int i=0; i<connections.size(); i++) {
            NetworkNode node = connections.get(i);
            if (!node.isDrawn()) {
                node.draw(g, offset * (connections.size()/-2 + i));
            }

            // Above
            if (y+height < node.y) {
                g.drawLine(x+width/2, y+height, node.x+node.getWidth()/2, node.y);
            }
            // Below
            else if (y > node.y+node.getHeight()) {
                g.drawLine(x+width/2, y, node.x+node.getWidth()/2, node.y+node.getHeight());
            }
            // Right
            else if (x-width > node.x) {
                g.drawLine(x, y+height/2, node.x+node.getWidth(), node.y+node.getHeight()/2);
            }
            // Left
            else if (x+width < node.x) {
                g.drawLine(x+width, y+height/2, node.x, node.y+node.getHeight()/2);
            }
            // Error
            else {
                System.err.println("Error: overlapping Network nodes.");
            }
        }
    }
}
