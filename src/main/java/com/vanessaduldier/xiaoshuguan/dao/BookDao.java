package com.vanessaduldier.xiaoshuguan.dao;

import com.vanessaduldier.xiaoshuguan.model.Author;
import com.vanessaduldier.xiaoshuguan.model.Book;
import com.vanessaduldier.xiaoshuguan.service.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object Book
 * @author Vanessa Duldier
 */
public class BookDao {
    private final AuthorDao authorDao = new AuthorDao();
    private final GenreDao genreDao = new GenreDao();

    public BookDao() {

    }

    /**
     * Insert a Book into Database
     */
    public Long insert(Book book) {
        return DatabaseService.executeTransactionWithResult(connection -> {
            // save authors
            List<Long> authorIds = new ArrayList<>();
            for (Author author : book.getAuthors()) {
                Long authorId = authorDao.insert(connection, author);
                authorIds.add(authorId);
            }

            // save book
            String sql = """
                INSERT INTO books (
                    title, author_id, file_path, 
                    description, publisher, isbn
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

            Long bookId;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, book.getTitle());
                ps.setLong(2, authorIds.isEmpty() ? 1L : authorIds.get(0));
                ps.setString(3, book.getFilePath());
                ps.setString(4, book.getDescription());
                ps.setString(5, book.getPublisher());
                ps.setString(6, book.getIsbn());

                ps.executeUpdate();

                // Für SQLite: letzte ID abrufen
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        bookId = rs.getLong(1);
                    } else {
                        throw new SQLException("Keine ID generiert");
                    }
                }
            }

            // save many to many relations
            if (!authorIds.isEmpty()) {
                String bookAuthorSql = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(bookAuthorSql)) {
                    for (Long authorId : authorIds) {
                        ps.setLong(1, bookId);
                        ps.setLong(2, authorId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            return bookId;
        });
    }

    /**
     * Get all Books from Database
     */
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Bücher", e);
        }
        return books;
    }

    /**
     * Update an existing Book in the Database
     */
    public void update(Book book) {
        DatabaseService.executeTransaction(connection -> {

            // 1. Buchdaten
            updateBookTable(connection, book);

            // 2. Alte Autoren löschen
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM book_author WHERE book_id = ?")) {
                ps.setLong(1, book.getId());
                ps.executeUpdate();
            }

            // 3. Neue Autoren setzen
            for (Author author : book.getAuthors()) {
                Long authorId = authorDao.insert(connection, author);
                try (PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO book_author(book_id, author_id) VALUES (?, ?)")) {
                    ps.setLong(1, book.getId());
                    ps.setLong(2, authorId);
                    ps.executeUpdate();
                }
            }

            // 4. Genres
            genreDao.updateBookGenres(book, connection);
        });
    }

    private void updateBookTable(Connection connection, Book book) throws SQLException {
        String sql = """
        UPDATE books
        SET title = ?, description = ?, publisher = ?, isbn = ?
        WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getDescription());
            ps.setString(3, book.getPublisher());
            ps.setString(4, book.getIsbn());
            ps.setLong(5, book.getId());
            ps.executeUpdate();
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Long bookId = rs.getLong("id");

        // Autoren laden
        List<Author> authors = loadAuthors(bookId);

        // Buch mit Basisinformationen erstellen (keine Genres im neuen Schema)
        Book book = new Book(
                bookId,
                rs.getString("title"),
                authors,
                new ArrayList<>() // Keine Genres in vereinfachtem Schema
        );

        // Verfügbare Metadaten setzen
        book.setDescription(rs.getString("description"));
        book.setPublisher(rs.getString("publisher"));
        book.setIsbn(rs.getString("isbn"));
        book.setFilePath(rs.getString("file_path"));

        // genres mit GoodreadsService und GenreDao
        List<String> genres = genreDao.findGenresForBook(bookId);
        book.setGenres(genres);

        return book;
    }

    private List<String> extractGenresFromResultSet(ResultSet rs) throws SQLException {
        List<String> genres = new ArrayList<>();
        addIfNotNull(genres, rs.getString("main_genre"));
        addIfNotNull(genres, rs.getString("subgenre1"));
        addIfNotNull(genres, rs.getString("subgenre2"));
        addIfNotNull(genres, rs.getString("subgenre3"));
        return genres;
    }

    public Long findBookId(String title) {
        return null;
    }

    public Book findBook(Long id) {
        return null;
    }

    private List<Author> loadAuthors(Long bookId) {
        List<Author> authors = new ArrayList<>();
        String sql = """
                SELECT a.* FROM author a
                JOIN book_author ba ON a.id = ba.author_id
                WHERE ba.book_id = ?
                """;

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    authors.add(new Author(
                            rs.getLong("id"),
                            rs.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Autoren", e);
        }
        return authors;
    }

    // helper functions
    private void addIfNotNull(List<String> list, String item) {
        if (item != null && !item.trim().isEmpty()) {
            list.add(item);
        }
    }
}
