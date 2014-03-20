import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Noto {
    // Set the default size of the window
    private static final int WIDTH = 800;
    private static final int HEIGHT = 640;

    // Start the program
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

    // Set up the GUI
    public static void createAndShowGUI() {
        NotoFrame frame = new NotoFrame(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(true);
    }
}
