CREATE TABLE currency_exchange_rate (
    id INT PRIMARY KEY AUTO_INCREMENT,
    base_currency VARCHAR(50),
    quote_currency VARCHAR(50),
    update_time TIMESTAMP,
    average_bid DECIMAL(20,10),
    average_ask DECIMAL(20,10),
    high_bid DECIMAL(20,10),
    high_ask DECIMAL(20,10),
    low_bid DECIMAL(20,10),
    low_ask DECIMAL(20,10),
    UNIQUE (base_currency, quote_currency, update_time)
);
