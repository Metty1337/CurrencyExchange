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
SELECT BaseCurrencyId from ExchangeRates;