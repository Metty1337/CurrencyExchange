package metty1337.currencyexchange.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Currency {
    private Integer ID;
    private String Code;
    private String FullName;
    private String Sign;
}
