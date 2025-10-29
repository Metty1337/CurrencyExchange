package metty1337.currencyexchange.exceptions;

public class CurrencyAlreadyExists extends RuntimeException {
    public CurrencyAlreadyExists(String message) {
        super(message);
    }

    public CurrencyAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}
