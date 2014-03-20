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
        fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
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

    public String getOutputPath() {
        return getProjectPath() + "output/";
    }

    public String getImagePath() {
        return getOutputPath() + "img/";
    }

    public String getCSSPath() {
        return getOutputPath() + "css/";
    }

    public String getScriptPath() {
        return getOutputPath() + "scripts/";
    }

    public void newDocument() {
        currentFile = null;
        projectPath = "";
    }
}
