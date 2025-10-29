package metty1337.currencyexchange.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExceptionMessages {
    DatabaseException("Database error"),
    CurrencyDoesntExistException("Currency does not exist"),
    CurrencyAlreadyExists("Currency already exists");
    private final String message;
}
