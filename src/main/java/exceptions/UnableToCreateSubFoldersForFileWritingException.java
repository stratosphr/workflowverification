package exceptions;

public class UnableToCreateSubFoldersForFileWritingException extends RuntimeException {

    public UnableToCreateSubFoldersForFileWritingException() {
        super("Unable to create subfolders for writing to a file.");
    }

}
