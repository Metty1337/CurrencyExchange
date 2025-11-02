package metty1337.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class CurrencyDTO {
    private Integer id;
    private String name;
    private String code;
    private String sign;
}
