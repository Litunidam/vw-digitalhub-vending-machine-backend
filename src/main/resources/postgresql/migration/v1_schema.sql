CREATE SCHEMA IF NOT EXISTS vending;

CREATE TABLE dispensers
(
    dispenser_id     UUID PRIMARY key,
    dispenser_status VARCHAR(100)
);

CREATE TABLE products
(
    product_id         UUID PRIMARY KEY,
    product_name       VARCHAR(100)   NOT NULL,
    product_price      NUMERIC(19, 2) NOT NULL,
    product_stock      INTEGER        NOT NULL,
    product_expiration DATE           NOT NULL,
    dispenser_id       UUID           NOT NULL,
    CONSTRAINT fk_dispenser_product FOREIGN KEY (dispenser_id) REFERENCES dispensers (dispenser_id) ON DELETE CASCADE
);

CREATE TABLE coin_inventory
(
    coin_id      UUID PRIMARY KEY,
    coin_name    VARCHAR(20) NOT NULL,
    coin_count   INTEGER     NOT NULL,
    dispenser_id UUID        NOT NULL,
    CONSTRAINT fk_dispenser_coin_inventory FOREIGN KEY (dispenser_id) REFERENCES dispensers (dispenser_id) ON DELETE CASCADE
);
