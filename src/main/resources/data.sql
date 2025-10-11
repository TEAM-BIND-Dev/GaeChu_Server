-- Seed initial categories
INSERT INTO categories (category_id, category_name)
VALUES (1, 'GENERAL'),
       (2, 'NOTICE'),
       (3, 'QNA'),
       (4, 'FREE'),
       (5, 'EVENT')
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);
