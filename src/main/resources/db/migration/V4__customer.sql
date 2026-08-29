create table customer
(
    tg_user_id BIGINT PRIMARY KEY,
    phone      TEXT,
    full_name  TEXT,
    update_at  TIMESTAMP not null DEFAULT now()
);