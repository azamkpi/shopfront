CREATE TABLE shop
(
    id            BIGSERIAL PRIMARY KEY,
    slug          TEXT UNIQUE NOT NULL,
    title         TEXT        NOT NULL,
    admin_chat_id BIGINT,
    currency      TEXT        NOT NULL DEFAULT 'UZS',
    welcome_text  TEXT,
    phone         TEXT,
    address       TEXT,
    created_at    TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE TABLE category
(
    id          BIGSERIAL PRIMARY KEY,
    shop_id     BIGINT    NOT NULL REFERENCES shop (id),
    category_id BIGINT REFERENCES category (id),
    name        TEXT      NOT NULL,
    slug        TEXT      NOT NULL,
    description TEXT,
    negotiable  BOOLEAN   NOT NULL DEFAULT FALSE,
    active      BOOLEAN   NOT NULL DEFAULT TRUE,
    views       INT       NOT NULL DEFAULT 0,
    sort_order  INT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE product
(
    id          BIGSERIAL PRIMARY KEY,
    shop_id     BIGINT    NOT NULL REFERENCES shop (id),
    category_id BIGINT REFERENCES category (id),
    name        TEXT      NOT NULL,
    description TEXT,
    negotiable  BOOLEAN   NOT NULL DEFAULT FALSE,
    active      BOOLEAN   NOT NULL DEFAULT TRUE,
    views       INT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_shop_active ON product (shop_id, active, created_at DESC);

CREATE TABLE product_variant
(
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT  NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    attributes JSONB   NOT NULL DEFAULT '{}'::jsonb,
    price      NUMERIC(14, 2),
    old_price  NUMERIC(14, 2),
    stock_qty  INT,
    in_stock   BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT     NOT NULL DEFAULT 0
);

CREATE INDEX idx_variant_product ON product_variant (product_id);

CREATE TABLE product_image
(
    id             BIGSERIAL PRIMARY KEY,
    product_id     BIGINT NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    file_id        TEXT   NOT NULL,
    file_unique_id TEXT,
    sort_order     INT    NOT NULL DEFAULT 0
);

CREATE INDEX idx_image_product ON product_image (product_id, sort_order);

CREATE TABLE order_request
(
    id         BIGSERIAL PRIMARY KEY,
    shop_id    BIGINT    NOT NULL REFERENCES shop (id),
    variant_id BIGINT REFERENCES product_variant (id),
    tg_user_id BIGINT,
    full_name  TEXT,
    phone      TEXT,
    note       TEXT,
    status     TEXT      NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_shop_status ON order_request (shop_id, status, created_at DESC);
