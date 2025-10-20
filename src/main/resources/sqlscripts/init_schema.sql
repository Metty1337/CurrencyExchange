CREATE TABLE IF NOT EXISTS Currencies (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Code VARCHAR(30) NOT NULL UNIQUE,
    FullName VARCHAR(60) NOT NULL,
    Sign VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS ExchangeRates (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId INTEGER NOT NULL,
    TargetCurrencyId INTEGER NOT NULL,
    Rate DECIMAL(6) NOT NULL,
    UNIQUE (BaseCurrencyId, TargetCurrencyId),
    CHECK (BaseCurrencyId != TargetCurrencyId),
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies (ID),
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies (ID)
);