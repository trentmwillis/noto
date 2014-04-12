import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

/*
This class represent pie charts. It is a fairly straightforward class,
with methods only to add data to the chart and draw it.
*/

public class Plot extends Diagram {
    private ArrayList<Shape> shapes;            // Stores the shapes on the plot
    private String title;                       // Title of the chart, if defined
    private Color color;                        // Base Color of the chart, if defined

    // Constructor
    public Plot() {
        super();

        // Init data members
        shapes = new ArrayList<Shape>();
        title = "";
        color = Color.BLUE;
    }

    public void addData(String data) throws BuildException {
        // Set up the scanner for the data
        Scanner dataScanner = new Scanner(data);
        String delimiters = "[\\(,\\)]";

        try {
            dataScanner.useDelimiter(delimiters);

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
            }

            float x = Float.parseFloat(dataScanner.next().trim());
            float y = Float.parseFloat(dataScanner.next().trim());

            if (dataScanner.hasNext()) {
                dataScanner.next();
                float x2 = Float.parseFloat(dataScanner.next().trim());
                float y2 = Float.parseFloat(dataScanner.next().trim());

                shapes.add(new Line2D.Float(x,y,x2,y2));
            } else {
                shapes.add(new Ellipse2D.Float(x - 2.5f, y - 2.5f, 5f, 5f));
            }
        }

        // This will occur when the value for the percentage is incorrect
        catch (NumberFormatException e) {
            throw new BuildException("Diagram #" + id + " (Plot): One of the values is not a proper number");
        }

        // This will occur when they give a bad color name
        catch (NoSuchFieldException e) {
            throw new BuildException("Diagram #" + id + " (Plot): Unsupported color choice");
        }

        // This will occur when any other syntax violation occurs
        catch (Exception e) {
            throw new BuildException("Diagram #" + id + " (Plot): syntax error\n" + e.toString());
        }
    }

    protected void draw() {
        int width = 500;                            // Width of the Pie, NOT the image
        int height = 500;                           // Height of the Pie
        int imgHeight = (title != "") ? 600 : 500;  // Height of the image, different from
                                                    // the other height if a title is specified

        // Create a new image
        image = new BufferedImage(Html.PAGE_WIDTH, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Html.PAGE_WIDTH, imgHeight);

        // Turn on Anti-Aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(color);
        for (int i=0; i<shapes.size(); i++) {
            if (shapes.get(i) instanceof Line2D.Float) {
                Line2D.Float line = (Line2D.Float)shapes.get(i);
                g.fill(new Ellipse2D.Float((float)line.getX1() - 2.5f, (float)line.getY1() - 2.5f, 5f, 5f));
                g.fill(new Ellipse2D.Float((float)line.getX2() - 2.5f, (float)line.getY2() - 2.5f, 5f, 5f));
                g.draw(line);
            } else {

                g.fill(shapes.get(i));
            }
        }

        // If there is a title, draw it
        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, width/2 - g.getFontMetrics().stringWidth(title)/2, height + 40);
        }
    }
}
