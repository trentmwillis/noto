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

    protected void draw() {
        int width = 500;
        int height = 500;
        int legendWidth = 200;

        // Create a new image
        image = new BufferedImage(width + legendWidth, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        // Fill in background with white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width + legendWidth, height);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw sections
        int startAngle = 0;
        int endAngle = 0;
        int x = width;
        int y = 0;
        double arcAngle = 0.0;

        Color color = Color.RED;
        g.setColor(color);
        for (int key : values.keySet()) {
            startAngle = endAngle;
            arcAngle = key * 3.6;
            endAngle += arcAngle;

            g.fillArc(0, 0, width, height, startAngle, (int) arcAngle);

            // Draw label
            y += 20;
            g.fillRect(x,y,20,20);
            g.setColor(Color.BLACK);
            g.drawString(values.get(key), x + 20, y + 14);

            color = color.darker();
            g.setColor(color);
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
