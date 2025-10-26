package metty1337.currencyexchange.mapper;

import lombok.NoArgsConstructor;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.models.Currency;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CurrencyMapper {
    public CurrencyDTO toDTO(Currency currency) {
        return new CurrencyDTO(
                currency.getID(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }

    public Currency toModel(CurrencyDTO currencyDTO) {
        return new Currency(
                currencyDTO.getId(),
                currencyDTO.getCode(),
                currencyDTO.getName(),
                currencyDTO.getSign()
        );
    }

    public List<CurrencyDTO> toDTO(List<Currency> currencies) {
        List<CurrencyDTO> currencyDTO = new ArrayList<>();
        for (Currency currency : currencies) {
            currencyDTO.add(toDTO(currency));
        }
        return currencyDTO;
    }
}
