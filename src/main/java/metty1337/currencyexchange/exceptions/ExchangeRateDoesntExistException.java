package metty1337.currencyexchange.exceptions;

public class ExchangeRateDoesntExistException extends RuntimeException {
    public ExchangeRateDoesntExistException(String message) {
        super(message);
    }
}
