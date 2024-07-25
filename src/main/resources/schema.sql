CREATE TABLE IF NOT EXISTS conversion_history
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id   VARCHAR(255) NOT NULL,
    source_currency  VARCHAR(3)   NOT NULL,
    target_currency  VARCHAR(3)   NOT NULL,
    amount           BIGINT       NOT NULL,
    converted_amount BIGINT       NOT NULL,
    transaction_date TIMESTAMP    NOT NULL
);
