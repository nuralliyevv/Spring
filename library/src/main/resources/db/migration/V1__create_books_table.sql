CREATE TABLE books (
                       id             BIGSERIAL PRIMARY KEY,
                       title          VARCHAR(200) NOT NULL,
                       author         VARCHAR(100) NOT NULL,
                       isbn           VARCHAR(13)  NOT NULL,
                       published_year INTEGER      NOT NULL,
                       CONSTRAINT uq_books_isbn UNIQUE (isbn)
);