package exceptions;

public class InvalidSpecificationFolderException extends RuntimeException {

    public InvalidSpecificationFolderException() {
        super("The specified specification folder is invalid.");
    }

}
