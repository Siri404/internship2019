package internshipCoera.Exceptions;

public class UnnecessaryFundsException extends Exception {
    public UnnecessaryFundsException(String message) {
        super(message);
    }

    public UnnecessaryFundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnnecessaryFundsException(Throwable cause) {
        super(cause);
    }
}
