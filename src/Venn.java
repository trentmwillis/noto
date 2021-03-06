import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
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

    // Title of the chart, if any
    private String title;

    // Variable for the size of the diagram image
    private int width;
    private int height;

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
        title = "";
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

        // Check if setting the title or color
        if (value.equals("title")) {
            title = dataScanner.next().trim();
            return;
        }

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

    // Draw the Venn diagram
    protected void draw() {
        // Width is equal to the widest an image can be
        width = Html.PAGE_WIDTH;
        // Set height equal to the maximum depth level * a constant
        height = (title != "") ? 600 : 500;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set up constants to draw the diagram
        int diameter = 500;
        int x1 = 0;
        int x2 = diameter - (diameter/3);
        int y;

        // Draw left circle
        g.setColor(new Color(255, 0, 0, 128));
        g.fillOval(x1, 0, diameter, diameter);

        // Draw middle content
        int stringWidth;
        g.setColor(Color.BLACK);
        y = (diameter/2) - (left.size()/2)*20;
        for (String value : left) {
            stringWidth = g.getFontMetrics().stringWidth(value);
            g.drawString(value, x2 - stringWidth - 20, y);
            y += 20;
        }

        // Draw right circle
        g.setColor(new Color(0, 0, 255, 128));
        g.fillOval(x2, 0, diameter, diameter);

        //Draw right content
        y = (diameter/2) - (right.size()/2)*20;
        g.setColor(Color.BLACK);
        for (String value : right) {
            stringWidth = g.getFontMetrics().stringWidth(value);
            g.drawString(value, diameter + 20, y);
            y += 20;
        }

        // Draw middle content
        y = (diameter/2) - (middle.size()/2)*20;
        for (String value : middle) {
            FontRenderContext frc = g.getFontRenderContext();
            LineBreakMeasurer measurer = new LineBreakMeasurer(new AttributedString(value).getIterator(), frc);
            float wrappingWidth = diameter - x2 - 20;
            while (measurer.getPosition() < value.length()) {
                TextLayout layout = measurer.nextLayout(wrappingWidth);
                layout.draw(g, (diameter+x2)/2 - layout.getAdvance()/2, y);
                y += 20;
            }
        }

        // If there is a title, draw it
        if (title != "") {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(24f));
            g.drawString(title, (diameter+x2)/2 - g.getFontMetrics().stringWidth(title)/2, diameter + 70);
        }
    }
}
