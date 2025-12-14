CREATE TABLE IF NOT EXISTS books (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    isbn TEXT,
    year INTEGER,
    genre TEXT,
    pages INTEGER,
    rating REAL CHECK (rating >= 0 AND rating <= 5),
    status TEXT DEFAULT 'unread' CHECK (status IN ('unread', 'reading', 'read')),
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
    );

CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);