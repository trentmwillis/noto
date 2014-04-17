
import java.io.File;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*
This is the main class that gets the program up and running.
Really simple, just initializes the GUI on the event dispatch thread.
*/

public class Noto {
    // Set the default size of the window
    private static final int WIDTH = 800;
    private static final int HEIGHT = 640;
    private static NotoFrame frame;

    // Start the program
	public static void main(String[] args) {
        if (args.length == 0) {
            // Create the GUI on the EDT
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    initGUI();
                }
            });    
        } else {
            switch(args[0]) {
                case "-b":
                    buildFile(args[1], true);
                    break;
                case "-B":
                    buildFile(args[1], false);
                    break;
                case "-p":
                    buildProject(args[1]);
                    break;
                case "-o":
                    openFile(args[1]);
                    break;
                default:
                    System.out.println("\nNoto Command-Line Reference");
                    System.out.println("Arguments          Function");
                    System.out.println("---------------------------");
                    System.out.println("''                 Start editor");
                    System.out.println("'-b <file-path>'   Build single file");
                    System.out.println("'-B <file-path>'   Build single file in project");
                    System.out.println("'-o <file-path>'   Open file in editor");
                    System.out.println("'-p <dir-path>'    Build entire project (assume project has index.txt)\n");
                    break;
            }
        }
	}

    // Set up the GUI
    private static void initGUI() {
        // Create the frame for the program
        frame = new NotoFrame(WIDTH, HEIGHT);

        // Set some default options for the program
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    private static void buildFile(String filePath, boolean single) {
        File file = new File(filePath);
        if (file.exists()) {
            ProjectManager.getInstance().setCurrentOpenFile(file);
            Builder.getInstance().buildSingle(single);
        } else {
            System.out.println("File " + filePath + " does not exist.");
        }
    }

    private static void buildProject(String dirPath) {
        String filePath = (dirPath.endsWith("/")) ? dirPath + "index.txt" : dirPath + "/index.txt";
        File file = new File(filePath);
        if (file.exists()) {
            ProjectManager.getInstance().setCurrentOpenFile(file);
            Builder.getInstance().buildAll();
        } else {
            System.out.println("Directory " + dirPath + " either does not exist or does not contain an index.txt file.");
        }
    }

    private static void openFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            initGUI();
            frame.open(file);
        } else {
            System.out.println("File " + filePath + " does not exist.");
        }
    }
}
