package metty1337.currencyexchange.exceptions;


public class DatabaseException extends RuntimeException {
    private String message;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
