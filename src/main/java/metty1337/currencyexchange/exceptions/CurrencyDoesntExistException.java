package metty1337.currencyexchange.exceptions;

public class CurrencyDoesntExistException extends RuntimeException {
    public CurrencyDoesntExistException(String message) {
        super(message);
    }

    public CurrencyDoesntExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
