package metty1337.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Setter
@Getter
public class ExchangeRateDTO {
    private Integer id;
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;
}
