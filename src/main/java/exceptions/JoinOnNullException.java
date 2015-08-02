package exceptions;

public class JoinOnNullException extends RuntimeException {

    public JoinOnNullException(){
        super("Joining an empty array is forbidden.");
    }

}
