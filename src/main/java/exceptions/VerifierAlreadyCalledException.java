package exceptions;

public class VerifierAlreadyCalledException extends RuntimeException {

    public VerifierAlreadyCalledException(){
        super("A verifier should only be called to work once. You must instantiate a new verifier.");
    }

}
