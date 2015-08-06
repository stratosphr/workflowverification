package exceptions;

public class AttemptToWriteNullPointerToFileException extends Exception {

    public AttemptToWriteNullPointerToFileException(){
        super("Attempt to write null pointer to a file. Nothing will be written.");
    }

}
