INSERT INTO categories (id, name, description) VALUES (1, "Detective", "Adventures of detectives");

INSERT INTO books (id, title, author, price, isbn)
VALUES (1, "White Fang", "Jack London", 299, "12587946831871");

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);