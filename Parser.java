public class Parser {
    private static Parser instance;

    private static Diagram building;
    private static boolean isBuilding;
    private static boolean complete;

    protected Parser() { /* Nothing */ }

    public static Parser getInstance() {
        if (instance == null) {
            instance = new Parser();
        }
        return instance;
    }

    public static void reset() {
        building = null;
        isBuilding = false;
        complete = false;
    }

    public static void newDiagram(DiagramType type) {
        // Use reflection?
        switch (type) {
            case TREE : building = new Tree();
                        break;
            case NETWORK : building = new Network();
                        break;
            case VENN : building = new Venn();
                        break;
            case PIE : building = new Pie();
                        break;
            case FLOWCHART : building = new Flowchart();
                             break;
            default :   building = new Tree();
        }
        isBuilding = true;
    }

    public static void addData(String data) {
        if (data.startsWith("}")) {
            complete = true;
            isBuilding = false;
            return;
        }

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

    public static String testing() {
        return building.toString();
    }

    public static boolean isBuilding() {
        return isBuilding;
    }

    public static boolean complete() {
        return complete;
    }
}
