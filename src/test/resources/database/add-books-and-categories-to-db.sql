INSERT INTO categories (id, name, description) VALUES (1, "Detective", "Adventures of detectives");
INSERT INTO categories (id, name, description) VALUES (2, "Verse", "Ukrainian poems");

INSERT INTO books (id, title, author, price, isbn)
VALUES (1, "Black Fang", "Jack London", 249, "12587946832236");

INSERT INTO books (id, title, author, price, isbn)
VALUES (2, "Sleep", "Taras Shevchenko", 199, "125876332278915");

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (2, 2);