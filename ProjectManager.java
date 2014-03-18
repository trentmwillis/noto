import java.io.File;

public class ProjectManager {
    private static ProjectManager instance;

    private File currentFile;
    private String fileName;
    private String projectPath;

    protected ProjectManager() { /* Nothing */ }

    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    public void setCurrentFile(File file) {
        currentFile = file;
        projectPath = currentFile.getParent();
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public String getFileName() {
        return fileName;
    }

    public String getProjectPath() {
        return projectPath + "/";
    }

    public void newDocument() {
        currentFile = null;
        projectPath = "";
    }
}
