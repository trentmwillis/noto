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

    // Start the program
	public static void main(String[] args) {
        // Create the GUI on the EDT
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });
	}

    // Set up the GUI
    private static void initGUI() {
        // Create the frame for the program
        NotoFrame frame = new NotoFrame(WIDTH, HEIGHT);

        // Set some default options for the program
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(true);
    }
}
