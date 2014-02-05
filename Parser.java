public class Parser {
    private static Diagram building;

    public static void newDiagram(Class type) {
        // Use reflection?
    }

    public static void addData(String data) {
        if (building != null) {
            building.addData(data);     // Add more data to the diagram
        }
    }

    public static String getImage() {
        if (building != null) {
            building.makeImage();           // Saves out a .png of the diagram
            return building.getImagePath(); // Return relative path to diagram
        }

        return "";
    }
}
