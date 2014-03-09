import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import java.util.Scanner;
import java.util.HashMap;

public class Pie extends Diagram {
    private HashMap<Integer, String> values;

    public Pie() {
        super();
        values = new HashMap<Integer, String>();
    }

    public void addData(String data) {
        Scanner dataScanner = new Scanner(data);
        dataScanner.useDelimiter(":");

        String value = dataScanner.next().trim();
        int percentage = Integer.parseInt(value);
        value = dataScanner.next();

        values.put(percentage, value);
    }

    /* Drawing Methods */

    private int top = 0;
    private int indent = 50;
    private int width, height;
    private int halfHeight = height / 2;
    private int nudge = 10;

    protected void draw() {
        // Width is equal to the widest an image can be
        width = Html.PAGE_WIDTH;
        // Set height equal to the maximum depth level * a constant
        height =  360;

        // Create a new image
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Set image color
        g.setColor(Color.BLACK);

        // Draw diagrams
        for (int key : values.keySet()) {

        }

        for (String value : values.values()) {

        }
    }

    // private void drawSection(Graphics g, ArrayList<String> values, int xOffset) {
    //     // Draw oval container
    //     g.drawOval(xOffset, 0, width / 3, height / 2);

    //     // Fill in values
    //     int count = 0;
    //     for (String value : values) {
    //         g.drawString(value, xOffset, 20 * count++);
    //     }
    // }
}
