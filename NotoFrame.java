import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

/*
This is the frame class; it handles all the GUI components
for the program and so also handles all calling all the important
functions.
*/

public class NotoFrame extends JFrame {
    // File choosers and text areas are expensive to create,
    // so just have one of each and don't create inside methods
    private JFileChooser fileChooser = new JFileChooser();
    private JTextArea textArea = new JTextArea();

    // Writers and reader will be swapped out whenever a new
    // file is loaded, but need to be shared between methods
    private FileWriter writer;
    private FileReader reader;
    
    // Keep track of if the file needs to be saved
    private boolean edited;

    // Undo/redo functionality
    private UndoManager undo = new UndoManager();
    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();

    // Constructor
    public NotoFrame(int width, int height) {
        setTitle("Noto - Note-Taking App");
        setSize(width, height);

        edited = false;

        initUndoManager();
        initMenu();
        initTextArea();
        initKeyListeners();
    }

    //  Nested class to represent undoable actions
    class UndoAction extends AbstractAction {
        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
                setEdited(true);
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    // Nested class to represent redoable actions
    class RedoAction extends AbstractAction {
        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    // Function to initialize all the undo/redo components
    private void initUndoManager() {
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                //Remember the edit and update the menus7
                undo.addEdit(e.getEdit());
                undoAction.updateUndoState();
                redoAction.updateRedoState();
            }
        });
    }

    /* Set up the textarea/editing components */
    private void initTextArea() {
        textArea.setLineWrap(true);
        textArea.setMargin(new Insets(12,6,12,6));
        textArea.setFont(new Font("Courier", Font.PLAIN, 14));
        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { setEdited(true); }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}
        });
        setContentPane(new JScrollPane(textArea));
    }

    /* Set the key shortcuts for menu options */
    private void initKeyListeners() {
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control S"),
            new AbstractAction() {
                public void actionPerformed(ActionEvent event) {
                    saveCurrent();
                }
            });

        textArea.getInputMap().put(KeyStroke.getKeyStroke("control N"),
            new AbstractAction() {
                public void actionPerformed(ActionEvent event) {
                    newDocument();
                }
            });

        textArea.getInputMap().put(KeyStroke.getKeyStroke("control O"),
            new AbstractAction() {
                public void actionPerformed(ActionEvent event) {
                    open();
                }
            });

        textArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), undoAction);
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control shift Z"), redoAction);
    }

    /* Set up the menus */
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem("New");

        /* File Menu */
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (userCheck("create a new document")) {
                    return;
                }
                newDocument();
            }
        });
        menu.add(item);

        item = new JMenuItem("Save");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                saveCurrent();
            }
        });
        menu.add(item);

        item = new JMenuItem("Save As...");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                save();
            }
        });
        menu.add(item);

        item = new JMenuItem("Open");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (userCheck("open a different document")) {
                    return;
                }
                open();
            }
        });
        menu.add(item);

        item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (userCheck("close the program")) {
                    return;
                }
                System.exit(0);
            }
        });
        menu.add(item);

        menuBar.add(menu);

        /* Edit Menu */
        menu = new JMenu("Edit");
        menu.add(undoAction);
        menu.add(redoAction);

        menuBar.add(menu);

        /* Build Menu */
        menu = new JMenu("Build");

        item = new JMenuItem("Build Single");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Builder.getInstance().buildSingle(true);
            }
        });
        menu.add(item);

        item = new JMenuItem("Build Single in Project");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Builder.getInstance().buildSingle(false);
            }
        });
        menu.add(item);

        item = new JMenuItem("Build Project");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Builder.getInstance().buildAll();
            }
        });
        menu.add(item);

        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    public boolean userCheck(String operation) {
        return edited && (JOptionPane.showConfirmDialog(null,
                    "You're file has unsaved changes, are you sure you want to " + operation + "?",
                    "Unsaved Changes Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                    ) != JOptionPane.YES_OPTION);
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
        if (edited) {
            setTitle("Noto - Note-Taking App - Edited*");
        } else {
            setTitle("Noto - Note-Taking App");
        }
    }

    /* Perform setup for a new document */
    private void newDocument() {
        fileChooser.setSelectedFile(null);
        ProjectManager.getInstance().newDocument();
        textArea.setText("");
        setEdited(false);
    }

    /* Perform a save operation on the open file */
    private void save() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File currentFile = fileChooser.getSelectedFile();
            ProjectManager.getInstance().setCurrentFile(currentFile);

            try {
                if (currentFile.exists()) {
                    // Display warning that file exists
                    JOptionPane.showConfirmDialog(this,
                        "You are about to overwrite " + currentFile.getName() + ". Is this okay?",
                        "File Overwrite Warning",
                        JOptionPane.WARNING_MESSAGE);
                }

                currentFile.createNewFile();
                writer = new FileWriter(currentFile);
                textArea.write(writer);
                writer.flush();
                writer.close();

                setEdited(false);
            } catch (IOException e) {
                System.out.println("Error saving: " + e.toString());
            }
            
        }
    }

    private void saveCurrent() {
        if (ProjectManager.getInstance().getCurrentFile() != null) {
            try {
                writer = new FileWriter(ProjectManager.getInstance().getCurrentFile());
                textArea.write(writer);
                setEdited(false);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        } else {
            save();
        }
    }

    private void open() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File currentFile = fileChooser.getSelectedFile();
            ProjectManager.getInstance().setCurrentFile(currentFile);

            try {
                reader = new FileReader(currentFile);
                textArea.read(reader, currentFile);
                setEdited(false);
            } catch (FileNotFoundException e) {
                System.out.println(e.toString());
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
}
