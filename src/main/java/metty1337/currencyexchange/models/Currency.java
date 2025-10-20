package metty1337.currencyexchange.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Currency {
    private Integer ID;
    private String Code;
    private String FullName;
    private String Sign;
}
