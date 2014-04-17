import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
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
    protected void draw() {
        // Width is equal to the widest an image can be
        int width = Html.PAGE_WIDTH;

        // Height needs to be 2 longest strings put together plus the radius of the circle
        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        int r = nodes.size() * 10;
        int max1 = g.getFontMetrics().stringWidth(nodes.get(0).getValue());
        int max2 = g.getFontMetrics().stringWidth(nodes.get(1).getValue());
        for (int i=2; i<nodes.size(); i++) {
            int length = g.getFontMetrics().stringWidth(nodes.get(i).getValue());
            if (length > max1) {
                if (max1 > max2) {
                    max2 = max1;
                }

                max1 = length;
            } else if (length > max2) {
                max2 = length;
            }
        }
        int height = max1 + max2 + (2*r);

        height += (title != "") ? 100 : 0;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(Color.BLACK);

        NetworkNode node;
        int cx = width/2;
        int cy = (title != "") ? (height-100)/2 : height/2;
        double theta = 0;
        double dTheta = 360/nodes.size();

        for (int i=0; i<nodes.size(); i++) {
            node = nodes.get(i);
            node.x = (int)Math.round(r * Math.cos(Math.toRadians(theta))) + cx;
            node.y = (int)Math.round(r * Math.sin(Math.toRadians(theta))) + cy;

            AffineTransform orig = g.getTransform();
            g.translate(cx, cy);
            if (theta > 90 && theta < 270) {
                g.rotate(Math.toRadians(theta));
                g.scale(-1, -1);
                g.drawString(node.getValue(), -r - g.getFontMetrics().stringWidth(node.getValue()), 0);
            } else {
                g.rotate(Math.toRadians(theta));
                g.drawString(node.getValue(), r, 0);
            }

            g.setTransform(orig);

            theta += dTheta;
        }

        // Draw connection lines
        for (int i=0; i<nodes.size(); i++) {
            node = nodes.get(i);
            g.setColor(color);
            for (int j=0; j<node.getConnections().size(); j++) {
                NetworkNode connectionNode = node.getConnections().get(j);
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(node.x, node.y, cx, cy, connectionNode.x, connectionNode.y);
                g.draw(q);
            }
            color = color.darker();
        }

        // Draw the title if set
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
    public int x, y;

    public NetworkNode(String value) {
        this.value = value;
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
}
