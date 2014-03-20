import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Diagram {
    public static final DiagramType[] TYPES = {
        DiagramType.TREE,
        DiagramType.FLOWCHART,
        DiagramType.NETWORK,
        DiagramType.VENN,
        DiagramType.PIE
    };

    protected static int globalID = 0;

    protected int id;
    protected File imgFile;
    protected BufferedImage image;

    public Diagram() {
        id = Diagram.globalID++;
    }

    public String getImagePath() {
        // Returns relative image path
        return imgFile.getPath();
    }

    public void makeImage() {
        draw();

        // Save out .png
        try {
            imgFile = new File(ProjectManager.getInstance().getProjectPath() + "img/" + id + ".png");
            ImageIO.write(image, "png", imgFile);
        } catch (IOException exception) {
            System.out.println("Exception: " + exception.toString());
            exception.printStackTrace();
        }
    }

    public abstract void addData(String data) throws BuildException; // Adds data to diagram

    protected abstract void draw();   // Creates the actual image
}
