INSERT INTO shop (slug, title, welcome_text, currency)
VALUES ('aroma', 'Aroma Parfum', 'Barcha turdagi parfumeriyalar', 'UZS');

INSERT INTO category(shop_id, name, slug, sort_order) VALUES
            (1, 'Erkaklar', 'erkaklar', 1),
            (1, 'Ayollar', 'ayollar', 2),
            (1, 'Unisex', 'Unisex', 3),
            (1, 'Arab parfum', 'arab', 4);