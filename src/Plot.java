import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

/*
This class represents point plots/line graphs. Pretty straightforward, both things
are simply stored as Shapes in an ArrayList.
*/

public class Plot extends Diagram {
    private static final float POINT_SIZE = 10.0f;  // Size of the points when drawn
    private static final float PLOT_SIZE = 500;     // Size of the plot, which are squares
    private ArrayList<Shape> shapes;                // Stores the shapes on the plot
    private String title;                           // Title of the chart, if defined
    private Color color;                            // Base color of the chart, if defined
    private float min, max;                         // Bounds of the points to map later

    // Constructor
    public Plot() {
        super();

        // Init data members
        shapes = new ArrayList<Shape>();
        title = "";
        color = Color.BLUE;
        min = max = 0;
    }

    public void addData(String data) throws BuildException {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter("[\\(,\\)]");      // Uses a simple regex for the delimiter

        try {
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

            // Read in initial values
            float x = Float.parseFloat(dataScanner.next().trim()) - POINT_SIZE/2;
            float y = Float.parseFloat(dataScanner.next().trim()) - POINT_SIZE/2;

            // If scanner has more values, do the stuff for a line
            if (dataScanner.hasNext()) {
                do {
                    updateMinMax(x,y);

                    dataScanner.next();
                    float x2 = Float.parseFloat(dataScanner.next().trim()) - POINT_SIZE/2;
                    float y2 = Float.parseFloat(dataScanner.next().trim()) - POINT_SIZE/2;

                    shapes.add(new Line2D.Float(x,y,x2,y2));

                    x = x2;
                    y = y2;
                } while (dataScanner.hasNext());

                updateMinMax(x,y);
            } else {
                updateMinMax(x,y);
                shapes.add(new Ellipse2D.Float(x, y, POINT_SIZE, POINT_SIZE));
            }
        }

        // This will occur when the format of one of the numbers is wrong
        catch (NumberFormatException e) {
            throw new BuildException("Diagram #" + id + " (Plot): One of the values is not a proper number.");
        }

        // This will occur when they give a bad color name
        catch (NoSuchFieldException e) {
            throw new BuildException("Diagram #" + id + " (Plot): Unsupported color choice.");
        }

        // This will occur when any other syntax violation occurs
        catch (Exception e) {
            throw new BuildException("Diagram #" + id + " (Plot): syntax error.\n" + e.toString());
        }
    }

    protected void draw() {
        int width = Html.PAGE_WIDTH;
        int height = (int)PLOT_SIZE;
        height += (title != "") ? 100 : 0;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Turn on Anti-Aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(color);
        for (int i=0; i<shapes.size(); i++) {
            if (shapes.get(i) instanceof Line2D.Float) {
                Line2D.Float line = (Line2D.Float)shapes.get(i);
                drawPoint(new Ellipse2D.Float((float)line.getX1(), (float)line.getY1(), POINT_SIZE, POINT_SIZE), g);
                drawPoint(new Ellipse2D.Float((float)line.getX2(), (float)line.getY2(), POINT_SIZE, POINT_SIZE), g);
                line = remap(line);
                g.draw(line);
            } else {
                drawPoint((Ellipse2D.Float)shapes.get(i), g);
            }
        }

        // If there is a title, draw it
        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, 250 - g.getFontMetrics().stringWidth(title)/2, height - 40);
        }
    }

    private void drawPoint(Ellipse2D.Float point, Graphics2D g) {
        float xVal = (float)point.getX() + POINT_SIZE/2;
        float yVal = (float)point.getY() + POINT_SIZE/2;
        point = remap(point);
        g.setColor(Color.BLACK);
        g.drawString("(" + xVal + ", " + yVal + ")", (float)point.getX() + POINT_SIZE, (float)point.getY() + POINT_SIZE);
        g.setColor(color);
        g.fill(point);
    }

    private Line2D.Float remap(Line2D.Float line) {
        float x1 = remap((float)line.getX1(), min, max);
        float x2 = remap((float)line.getX2(), min, max);
        float y1 = remap((float)line.getY1(), min, max);
        float y2 = remap((float)line.getY2(), min, max);

        return new Line2D.Float(x1, y1, x2, y2);
    }

    private Ellipse2D.Float remap(Ellipse2D.Float point) {
        float x = remap((float)point.getX(), min, max);
        float y = remap((float)point.getY(), min, max);
        return new Ellipse2D.Float(x - POINT_SIZE/2, y - POINT_SIZE/2, POINT_SIZE, POINT_SIZE);
    }

    private float remap(float value, float min, float max) {
        return (POINT_SIZE/2) + ((value-min) * (PLOT_SIZE-POINT_SIZE)) / (max - min);
    }

    private void updateMinMax(float x, float y) {
        max = Math.max(Math.max(x,y),max);
        min = Math.min(Math.min(x,y),min);
    }
}
