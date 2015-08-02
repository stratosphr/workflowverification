package exceptions;

public class InvalidSpecificationException extends RuntimeException {

    public InvalidSpecificationException(String message) {
        super("Le fichier xml représentant la spécification est invalide vis-à-vis du DTD \"specification.dtd\" :\n" + message);
    }

}
