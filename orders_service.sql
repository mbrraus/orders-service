CREATE DATABASE IF NOT EXISTS `orders_service`;
USE orders_service;

CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT,
    email VARCHAR(254) NOT NULL UNIQUE,
    full_name VARCHAR(160) NOT NULL,
    state VARCHAR(16) NOT NULL DEFAULT 'ACTIVE'
                      CHECK(state IN ('ACTIVE','BLOCKED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE order_header(
    id BIGINT AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'NEW'
                         CHECK ( status IN ('NEW','PAID','CANCELLED') ),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE order_item(
    id BIGINT AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_sku VARCHAR(64) NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    unit_price_snapshot DECIMAL(12,2) NOT NULL,
    qty INT NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (order_id) REFERENCES order_header(id) ON DELETE CASCADE
);



