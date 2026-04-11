-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Cart table
CREATE TABLE carts
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    date_created TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Cart item table
CREATE TABLE cart_items
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cart_id    UUID    NOT NULL,
    product_id BIGINT  NOT NULL,
    quantity   INTEGER NOT NULL CHECK (quantity > 0),

    CONSTRAINT fk_cart
        FOREIGN KEY (cart_id)
            REFERENCES carts (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE,

    CONSTRAINT unique_cart_product
        UNIQUE (cart_id, product_id)
);