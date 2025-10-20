package metty1337.currencyexchange;

import metty1337.currencyexchange.dao.DatabaseManager;
import metty1337.currencyexchange.models.Currency;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.init();

        Currency usd = new Currency(null, "USD", "US Dollar", "$");

    }
}
