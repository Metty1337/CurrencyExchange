package metty1337.currencyexchange.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExceptionMessages {
    DATABASE_EXCEPTION("Database error"),
    CURRENCY_DOESNT_EXIST_EXCEPTION("Currency does not exist"),
    CURRENCY_ALREADY_EXISTS("Currency already exists"),
    EXCHANGE_RATE_DOESNT_EXISTS("Exchange rate does not exist"),
    EXCHANGE_RATE_ALREADY_EXISTS("Exchange rate already exists");
    private final String message;
}
