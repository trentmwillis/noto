public abstract class Diagram {
    private File imgFile;
    private Image img;

    public String getImagePath() {
        // Returns relative image path
        return imgFile.getPath();
    }

    public void makeImage() {
        draw();

        // Save out .png
    }

    public abstract void addData(String data); // Adds data to diagram

    abstract void draw();   // Creates the actual image
}