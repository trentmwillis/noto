import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.HashMap;

/*
This class represent pie charts. It is a fairly straightforward class,
with methods only to add data to the chart and draw it.
*/

public class Pie extends Diagram {
    private HashMap<Integer, String> values;    // Stores values and their percentages
    private int currTotal;                      // Keeps track of total percentage
    private String title;                       // Title of the chart, if defined
    private Color color;                        // Base Color of the chart, if defined

    // Constructor
    public Pie() {
        super();

        // Init data members
        values = new HashMap<Integer, String>();
        currTotal = 0;
        title = "";
        color = Color.BLUE;
    }

    // This method adds data in the form: <percentage>: <value>
    public void addData(String data) throws BuildException {
        // Set up the scanner for the data
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter(":");

        try {
            // Read in the first value
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

            // Parse the percentage
            int percentage = Integer.parseInt(value);

            // Make sure percetages don't add to more than 100
            if (currTotal + percentage > 100) {
                throw new BuildException("Diagram #" + id + " (Pie): Percentages for pie diagram exceed 100");
            } else {
                currTotal += percentage;
            }

            // Read in value to be associated with the percentage
            value = dataScanner.nextLine().trim();

            // Add the key-value pair to the HashMap
            values.put(percentage, value);
        }

        // This will occur when the value for the percentage is incorrect
        catch (NumberFormatException e) {
            throw new BuildException("Diagram #" + id + " (Pie): One of the percentages is not a proper integer");
        }

        // This will occur when they give a bad color name
        catch (NoSuchFieldException e) {
            throw new BuildException("Diagram #" + id + " (Pie): Unsupported color choice");
        }

        // This will occur when any other syntax violation occurs
        catch (Exception e) {
            throw new BuildException("Diagram #" + id + " (Pie): syntax error\n" + e.toString());
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

        // Prep for drawing slices & labels
        int startAngle = 0;     // Start angle of pie slice
        int endAngle = 0;       // Ending angle of pie slice
        double arcAngle = 0.0;  // Angle of arc of pie slice
        int x = width;          // X position of label
        int y = 0;              // Y position of label
        StringBuilder label;    // String to hold the label
        g.setColor(color);

        // Draw each slices & label
        for (int key : values.keySet()) {
            // Update the angles
            startAngle = endAngle;
            arcAngle = key * 3.6;
            endAngle += arcAngle;

            // Draw the pie slice
            g.fillArc(0, 0, width, height, startAngle, (int) arcAngle);

            // Create label
            label = new StringBuilder(((Integer)key).toString());
            label.append("% ");
            label.append(values.get(key));

            // Draw the label
            y += 20;
            g.fillRect(x,y,20,20);
            g.setColor(Color.BLACK);
            g.drawString(label.toString(), x + 30, y + 14);

            // Update color for next slice
            color = color.darker();
            g.setColor(color);
        }

        // If there is a title, draw it
        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, width/2 - g.getFontMetrics().stringWidth(title)/2, height + 40);
        }
    }
}
