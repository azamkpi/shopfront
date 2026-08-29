ALTER TABLE order_request
    ADD COLUMN total NUMERIC(14, 2);

CREATE TABLE order_item
(
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES order_request (id) ON DELETE CASCADE,
    variant_id   BIGINT REFERENCES product_variant (id),
    product_name TEXT   NOT NULL,
    price        NUMERIC(14, 2),
    qty          INT    NOT NULL DEFAULT 1
);
CREATE INDEX idx_order_item_order ON order_item (order_id);