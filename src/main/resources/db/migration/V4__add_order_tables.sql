
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    CONSTRAINT orders_users_id_fk
        FOREIGN KEY (customer_id) REFERENCES users (id)
);

CREATE TABLE order_items (
     id BIGSERIAL PRIMARY KEY,
     order_id BIGINT NOT NULL,
     product_id BIGINT NOT NULL,
     unit_price NUMERIC(10, 2) NOT NULL,
     quantity INTEGER NOT NULL,
     total_price NUMERIC(10, 2) NOT NULL,
     CONSTRAINT order_items_orders_id_fk
         FOREIGN KEY (order_id) REFERENCES orders (id),
     CONSTRAINT order_items_products_id_fk
         FOREIGN KEY (product_id) REFERENCES products (id)
);