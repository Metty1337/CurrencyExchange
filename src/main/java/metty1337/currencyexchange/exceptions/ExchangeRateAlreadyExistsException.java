package metty1337.currencyexchange.exceptions;

public class ExchangeRateAlreadyExistsException extends RuntimeException {
    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }
    public ExchangeRateAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
