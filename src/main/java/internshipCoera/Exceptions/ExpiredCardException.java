package internshipCoera.Exceptions;

public class ExpiredCardException extends Exception{
    public ExpiredCardException(String message) {
        super(message);
    }

    public ExpiredCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredCardException(Throwable cause) {
        super(cause);
    }
}
