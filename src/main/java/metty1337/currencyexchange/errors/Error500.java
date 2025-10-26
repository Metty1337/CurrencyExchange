package metty1337.currencyexchange.errors;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Error500 {
    public final String message = "Internal Server Error";
}
