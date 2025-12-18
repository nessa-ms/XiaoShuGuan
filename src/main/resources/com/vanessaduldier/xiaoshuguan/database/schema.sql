CREATE TABLE IF NOT EXISTS author (
                                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      name TEXT NOT NULL UNIQUE,
                                      goodreads_author_id TEXT
);

CREATE TABLE IF NOT EXISTS books (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     title TEXT NOT NULL,
                                     author_id INTEGER,
                                     cover_image TEXT,

                                     pages INTEGER,
                                     isbn TEXT,
                                     published_date TEXT,
                                     publisher TEXT,
                                     description TEXT,

                                     file_path TEXT UNIQUE NOT NULL,
                                     file_hash TEXT,

    -- Genres
                                     main_genre TEXT,
                                     subgenre1 TEXT,
                                     subgenre2 TEXT,
                                     subgenre3 TEXT,

    -- Goodreads Integration
                                     goodreads_id TEXT,
                                     goodreads_url TEXT,

    -- User Data
                                     rating REAL CHECK (rating >= 0 AND rating <= 5),
    status TEXT DEFAULT 'unread' CHECK (status IN ('unread', 'reading', 'read', 'dnf')),
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    -- Relation
    FOREIGN KEY (author_id) REFERENCES author(id)
    );

CREATE TABLE IF NOT EXISTS book_author (
    book_id INTEGER,
    author_id INTEGER,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (author_id) REFERENCES author(id)
    );

CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_author_id ON books(author_id);