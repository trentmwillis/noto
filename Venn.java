import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;

/*
This class represents 2-circle Venn diagrams. While simple in
theory, the drawing got a bit tricky.
*/

public class Venn extends Diagram {
    // Constants to represent which side is currently
    // adding data
    private static final int NO_SIDE     = 0;
    private static final int LEFT_SIDE   = 1;
    private static final int RIGHT_SIDE  = 2;
    private static final int MIDDLE_SIDE = 3;

    // ArrayLists keep track of the values for each section
    private ArrayList<String> left;
    private ArrayList<String> right;
    private ArrayList<String> middle;

    // Keeps track of which side to add data to
    private int onSide;

    // Constructor
    public Venn() {
        super();

        // Init data members
        left = new ArrayList<String>();
        right = new ArrayList<String>();
        middle = new ArrayList<String>();
        onSide = NO_SIDE;
    }

    // This method adds data given in the form:
    // <side-number>:
    // <value-1>
    // ...
    // <value-n>
    public void addData(String data) throws BuildException {
        // Set up the scanner for the data
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter(":");

        // Read in the value (each line will have only one value)
        String value = dataScanner.next().trim();

        try {
            onSide = Integer.parseInt(value);
            return;
        } catch (NumberFormatException e) {
            dataScanner.reset();

            switch (onSide) {
                case LEFT_SIDE   : left.add(value);
                                   break;
                case RIGHT_SIDE  : right.add(value);
                                   break;
                case MIDDLE_SIDE : middle.add(value);
                                   break;
                default          : throw new BuildException("VENN DIAGRAM: invalid syntax");
            }
        }
    }

    /* Drawing Methods */
    private int width, height;

    protected void draw() {
        // Width is equal to the widest an image can be
        width = Html.PAGE_WIDTH;
        // Set height equal to the maximum depth level * a constant
        height =  Math.max(left.size(), right.size()) * 60;
        height = (height > 500) ? height : 500;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(Color.BLACK);

        // Draw diagrams
        g.setColor(new Color(255, 0, 0, 128));
        drawSection(g, left, 0);

        g.setColor(new Color(0, 255, 0, 128));
        drawSection(g, right, width/3);

        g.setColor(new Color(0,0,0,0));
        drawSection(g, middle, width/6);
    }

    private void drawSection(Graphics2D g, ArrayList<String> values, int xOffset) {
        // Draw oval container
        g.fillOval(xOffset, 0, 2*width/3, height);

        // Draw each of the values in the middle of the container
        int count = 0;
        int stringLength = 0;
        int middle = xOffset + (2*width/6);
        g.setColor(Color.BLACK);

        for (String value : values) {
            stringLength = g.getFontMetrics().stringWidth(value);
            g.drawString(value, middle - stringLength/2, (20 * count++) + 50);
        }
    }
}
