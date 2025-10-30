PRAGMA foreign_keys = ON;
INSERT INTO Currencies (Code, FullName, Sign)
VALUES ('USDd', 'US Dollar', '$asdfasdfasdfasdfasdf');

PRAGMA foreign_keys = ON;
INSERT INTO Currencies (Code, FullName, Sign)
VALUES ('AFN', 'Afghanistan Afghani', '
؋');

PRAGMA foreign_keys = ON;
INSERT INTO Currencies (Code, FullName, Sign)
VALUES ('BOB', 'Bolivia Bolíviano', '$b');

PRAGMA foreign_keys = ON;
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (1, 2, 140.123);

PRAGMA foreign_keys = ON;
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (3, 1, 0.1234567);

PRAGMA foreign_keys = ON;
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES (2, 3, 43.2342424);

PRAGMA foreign_keys = ON;
SELECT BaseCurrencyId from ExchangeRates;


PRAGMA foreign_keys = ON;
SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE BaseCurrencyId = 1 AND TargetCurrencyId = 2;
