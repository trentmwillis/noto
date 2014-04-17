import java.lang.Exception;

public class BuildException extends Exception {
    public BuildException(String message) {
        super(message);
    }
}

class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
